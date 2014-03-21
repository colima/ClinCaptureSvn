/* Customize page */
function toolbarToogle(e) {
	if ($(e).children().text() == "Hide") {
		$(e).parent().animate({
			right : "-430px"
		}, 500);
		$(e).children().html("Show");
	} else {
		$(e).parent().animate({
			right : "0px"
		}, 500);
		$(e).children().html("Hide");
	}
}

if (!Array.prototype.forEach) {
	Array.prototype.forEach = function(fn, scope) {
		for ( var i = 0, len = this.length; i < len; ++i) {
			fn.call(scope, this[i], i, this);
		}
	};
}

if (typeof String.prototype.trim !== 'function') {
	String.prototype.trim = function() {
		return this.replace(/^\s+|\s+$/g, '');
	};
}

function inicializeDnD() {
	$(".droptrue").sortable({
		connectWith : '.droptrue',
		opacity : 0.6,
		update : updatePostOrder
	});

	$("#toolbar, #layout1, #layout2").disableSelection();
	$(".widget .chart_wrapper a").attr("href", "#");
	$(".widget input[type=button]").remove();
	updatePostOrder();
}

function updatePostOrder() {
	var arr = [];
	$(".column1 div.widget").each(function() {
		arr.push($(this).attr('id'));
	});
	$('#postOrder1').val(arr.join(','));

	var arr2 = [];
	$(".column2 div.widget").each(function() {
		arr2.push($(this).attr('id'));
	});
	$('#postOrder2').val(arr2.join(','));

	var arr3 = [];
	$("#toolbar div.widget").each(function() {
		arr3.push($(this).attr('id'));
	});
	$('#unusedWidgets').val(arr3.join(','));
}

function removeWidget(elemet) {
	var itemToRemove = $(elemet).parent();
	$("#toolbar").prepend(itemToRemove);
	updatePostOrder();
}

function saveLayoutAndExit() {
	$.ajax({
		type : "POST",
		url : "saveHomePage",
		data : {
			orderInColumn1 : $("#postOrder1").val(),
			orderInColumn2 : $("#postOrder2").val(),
			unusedWidgets : $("#unusedWidgets").val(),
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function() {
			$("form[id=goToHomePage]").submit();
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}
/* /Customize page */

/* Initialization of widgets */
function initNdsAssignedToMeWidget() {
	var url = getCurentUrl();

	$.ajax({
		type : "POST",
		url : url + "initNdsAssignedToMeWidget",
		data : {
			userId : $("#userId").val(),
			studyId : $("#studyId").val()
		},
		success : function(html) {
			var array = html.split(',');
			var newNds = parseInt(array[0]);
			var updatedNds = parseInt(array[1]);
			var resolutionProposedDns = parseInt(array[2]);
			var closedNds = parseInt(array[3]);

			if (resolutionProposedDns != 0) {
				$(".dns_assigned_to_me td.optional").css("display",
						"table-cell");

				$(".dns_assigned_to_me a.optional").css("display",
						"inline-block");
			}

			var totalNds = newNds + updatedNds + closedNds
					+ resolutionProposedDns;
			captionLimit = countCaptionLimit(totalNds);
			var captionSelector = ".dns_assigned_to_me .captions td";
			setCaption(captionSelector, captionLimit);
			var valuesSecelctor = ".dns_assigned_to_me .stack";
			setStacksLenghts(valuesSecelctor, array, captionLimit);
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

function initEventsCompletionWidget(action) {
	var url = getCurentUrl();
	var lastElement = $(".events_completion input#ec_last_element").val();

	if (lastElement == undefined) {
		lastElement = 0;
	}

	$(".events_completion #events_completion_container").hide(500);

	$.ajax({
		type : "POST",
		url : url + "initEventsCompletionWidget",
		data : {
			lastElement : lastElement,
			studyId : $("#studyId").val(),
			action : action
		},
		success : function(html) {
			$(".events_completion #events_completion_container").html(html);
			var hasNext = $("#events_completion_form #ec_has_next").val();
			var hasPrevious = $("#events_completion_form #ec_has_previous").val();

			if (hasNext == "true") {
				$(".events_completion input#next").css("display", "block");
			} else {
				$(".events_completion input#next").css("display", "none");
			}

			if (hasPrevious == "true") {
				$(".events_completion input#previous").css("display", "block");
			} else {
				$(".events_completion input#previous").css("display", "none");
			}
			$(".events_completion #events_completion_container").show(500);

			var stack = $("#events_completion_container .stacked_bar");
			stack.each(function(entry) {
				var values = new Array();
				var total = 0;
				var stack = $(this).find("li");
				stack.each(function(index) {
					var currentValue = $(this).find(".hidden").html();
					if (currentValue) {
						values[index] = parseInt(currentValue);
						total += parseInt(currentValue);
					}
				});
				if (parseInt(total) != 0) {
					var currentBarClass = $(this).attr("barnumber");
					var selector = "#events_completion_container .stacked_bar[barnumber="
							+ (currentBarClass) + "] .stack";
					setStacksLenghts(selector, values, total);
				}
			});

			var element = document.getElementById('toolbar');

			if (element) {
				$(".events_completion .chart_wrapper a").attr("href", "#");
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}

function initSubjectStatusCount() {
	var url = getCurentUrl();

	$.ajax({
		type : "POST",
		url : url + "initSubjectStatusCount",
		data : {},
		success : function(html) {

		$(".subject_status_count #subject_status_count_container")
				.html(html);
					
		var element = document.getElementById('toolbar');
		var data = new google.visualization.DataTable();
		var availableSubjects = parseInt($("form#subjects_status_count #ssc_available").val());
		var signedSubjects = parseInt($("form#subjects_status_count #ssc_signed").val());
		var removedSubjects = parseInt($("form#subjects_status_count #ssc_removed").val());
		var lockedSubjects = parseInt($("form#subjects_status_count #ssc_locked").val());
		var totalSubjectsCount = availableSubjects + signedSubjects + removedSubjects + lockedSubjects;

		if (totalSubjectsCount != 0 ) {
			data.addColumn('string', 'Statuses');
			data.addColumn('number', 'Count');
			data.addRows([ [ ' - Available', availableSubjects ],
			               [ ' - Signed', signedSubjects ], 
			               [ ' - Removed', removedSubjects ],
			               [ ' - Locked', lockedSubjects ] ]);
	
	
			var options = getPieOptions([ '#7fd0ff', '#32a656','#ff0000', '#868686' ]);
			var subjectStatusChart = new google.visualization.PieChart(document.getElementById('subject_status_count_chart'));
	
			subjectStatusChart.draw(data, options);
	
			if (!element) {
	
				function selectHandler() {
					var selectedItem = subjectStatusChart.getSelection()[0];
	
					if (selectedItem) {
						var statusName = data.getValue(selectedItem.row, 0)
								.toString().split('-')[1].trim().toLowerCase();
						
						window.location.href = "ListStudySubjects?module=admin&maxRows=15&showMoreLink=false&findSubjects_tr_=true&findSubjects_p_=1&findSubjects_mr_=15&findSubjects_f_studySubject.status="
								+ statusName;
					}
				}
	
				google.visualization.events.addListener(subjectStatusChart, 'select', selectHandler);
			}
		} else {
			$("#subject_status_count_container").attr("height","50px");
		}
	},
	error : function(e) {
			console.log("Error:" + e);
		}
	});
}

function initStudyProgress() {
	var url = getCurentUrl();

	$.ajax({
		type : "POST",
		url : url + "initStudyProgress",
		data : {},
		success : function(html) {

			$(".study_progress #study_progress_container").html(html);

			var spScheduled = parseInt($(
					"#study_progress #sp_scheduled_count").val());
			var spDES = parseInt($(
					"#study_progress #sp_data_entry_started_count")
					.val());
			var spSDV = parseInt($(
					"#study_progress #sp_source_data_verified_count")
					.val());
			var spSigned = parseInt($(
					"#study_progress #sp_signed_count").val());
			var spCompleted = parseInt($(
					"#study_progress #sp_completed_count").val());
			var spSkipped = parseInt($(
					"#study_progress #sp_skipped_count").val());
			var spStopped = parseInt($(
					"#study_progress #sp_stopped_count").val());
			var spLocked = parseInt($(
					"#study_progress #sp_locked_count").val());
			var totalCount = spScheduled + spDES + spSDV + spSigned
					+ spCompleted + spSkipped + spStopped + spLocked;
			var element = document.getElementById('toolbar');
			var spData = new google.visualization.DataTable();

			if (totalCount != 0) {
				spData.addColumn('string', 'Statuses');
				spData.addColumn('number', 'Count');
				spData.addColumn('number', 'StatusId');
				spData.addRows([ [ ' - Scheduled', spScheduled, 1 ],
								[ ' - Data Entry Started', spDES, 3 ],
								[ ' - SDV-ed', spSDV, 9 ],
								[ ' - Signed', spSigned, 8 ],
								[ ' - Completed', spCompleted, 4 ],
								[ ' - Skipped', spSkipped, 6 ],
								[ ' - Stopped', spStopped, 5 ],
								[ ' - Locked', spLocked, 7 ] ]);

				var options = getPieOptions([ '#12d2ff', '#ffc700',
						'#8ac819', '#029f32', '#9439c4', '#ff6301',
						'#ff0000', '#868686' ]);
				var subjectStatusChart = new google.visualization.PieChart(
						document.getElementById('study_progress_chart'));

				subjectStatusChart.draw(spData, options);

				if (!element) {
					var d = new Date();
					var months = [ "Jan", "Feb", "Mar", "Apr", "May",
							"Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
							"Dec" ];
					var curr_date = d.getDate();
					var curr_month = months[parseInt(d.getMonth())];
					var curr_year = d.getFullYear();
					var startYear = parseInt(curr_year) - 10;
					var endDate = (('' + curr_date).length < 2 ? '0'
							: '')
							+ curr_date
							+ "-"
							+ curr_month
							+ "-"
							+ curr_year;
					var startDate = (('' + curr_date).length < 2 ? '0'
							: '')
							+ curr_date
							+ "-"
							+ curr_month
							+ "-"
							+ startYear;

					function selectHandler() {
						var selectedItem = subjectStatusChart
								.getSelection()[0];

						if (selectedItem) {
							window.location.href = "ViewStudyEvents?startDate="
									+ startDate
									+ "&endDate="
									+ endDate
									+ "&statusId="
									+ spData.getValue(selectedItem.row,
											2).toString()
									+ "&submitted=1";
						}
					}

					google.visualization.events
							.addListener(subjectStatusChart, 'select',
							selectHandler);
				}
			} else {
				$("#study_progress_container").attr("height", "50px");
			}
		},
		error : function(e) {
			console.log("Error:" + e);
		}
	});
}
/* /Initialization of widgets */

/* Supporting functions */
function countCaptionLimit(total) {
	if (total < 4) {
		total = 4;
	} else if (total % 2 == 0) {
		(total / 2) % 2 == 0 ? total : total = total + 2;
	} else {
		(total + 1) % 4 == 0 ? total = total + 1 : total = total + 3;
	}
	return total;
}

function setCaption(selector, maxValue) {
	var counter = 0;
	$(selector).each(function() {
		if (counter == 0) {
			$(this).html(0);
		} else if (counter == 1) {
			$(this).html(maxValue / 4);
		} else if (counter == 2) {
			$(this).html(maxValue / 2);
		} else if (counter == 3) {
			$(this).html(maxValue / 4 * 3);
		} else if (counter == 4) {
			$(this).html(maxValue);
		}

		counter++;
	});
}

function setStacksLenghts(selector, values, captionLimit) {
	var barWidth = parseInt($(selector).parent().parent().width());
	var unitSize = barWidth / captionLimit;
	var counter = 0;
	$(selector).each(
			function(index) {
				var stackWidth = parseInt(values[counter], 10) * unitSize;
				$(this).animate({
					width : stackWidth
				}, 500);

				$(this).find(".pop-up").css("margin-left",
						(parseInt(values[counter]) * unitSize) / 2).html(
						parseInt(values[counter]));

				counter++;
			});
}

function getPieOptions(sliceColors) {
	var options = {
		width : 450,
		height : 200,
		legend : {
			position : 'right',
			alignment : 'center',
			textStyle : {
				color : '#4D4D4D'
			}
		},
		pieSliceText : 'percents',
		pieStartAngle : 0,
		chartArea : {
			left : 10,
			top : 20,
			right : 20,
			width : "85%",
			height : "85%"
		},
		colors : sliceColors,
		fontSize : 11,
		fontName : 'Tahoma',
		tooltip : {
			textStyle : {
				color : '#4D4D4D',
				fontName : 'Tahoma',
				fontSize : 11,
				showColorCode : false
			}
		}
	};

	return options;
}

function getCurentUrl() {
	var urlTemp = new RegExp("^.*(pages)")
			.exec(window.location.href.toString());
	var url = "";

	if (urlTemp == null) {
		url = "pages/";
	} else {
		url = "";
	}
	return url;
}

function checkCookiesDialog() {
	var cookieEnabled = (navigator.cookieEnabled) ? true : false;
    if (typeof navigator.cookieEnabled == "undefined" && !cookieEnabled) { 
        document.cookie="testcookie";
        cookieEnabled = (document.cookie.indexOf("testcookie") != -1) ? true : false;
    }
	if (!cookieEnabled) {
		$(".widget>div>div").remove();
		$(".widget>div>table").remove();
		displayCookiesErrorMessage();
	}
}

function displayCookiesErrorMessage() {
	if ($("#confirmation").length == 0) {
		$("body").append("<div id=\"confirmation\" style=\"display: none;\">"
							+ "<div style=\"clear: both; text-align: justify;\">"
							+ "Cookies are disabled in your browser, some of widgets will not be shown. Please enable cookies."
							+ "</div>" + 
						"</div>");

		$("#confirmation").dialog({
			autoOpen : false,
			modal : true,
			resizable : false,
			height : 80,
			minHeight : 80,
			width : 450
		});
	}

	$("#confirmation").dialog("open");
	var color = $('*').find('a').css('color');
	if (color == 'rgb(170, 98, 198)' || color == '#AA62C6'
			|| color == '#aa62c6') {
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#AA62C6');
	}
	if (color == 'rgb(117, 184, 148)' || color == '#75b894'
			|| color == '#75B894') {
		$('.ui-dialog .ui-dialog-titlebar').find('span').css('color', '#75b894');
	}
}
/* /Supporting functions */
