/* ===================================================================================================================================================================================================================================================================================================================================================================================================================================================================
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO‚ÄôS ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 * =================================================================================================================================================================================================================================================================================================================================================================================================================================================================== */

/* ============================================
 * Creates an <div> element.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <div>
 *  - divClass - the css class for the <div>
 *
 * Returns the created div
 *
 * ============================================= */
function createDiv(params) {
	var div = $("<div/>");
	div.attr("id", params.id);
	div.addClass(params.divClass);
	return div;
}

/* =============================================
 * Creates a single <button>.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <button>
 *  - btnClass - the css class for the <button>
 *  - text - the button display <button>
 *
 * Returns the created button
 * ============================================== */
function createButton(params) {
	var btn = $("<button/>");
	btn.attr("id", params.id);
	btn.addClass(params.btnClass);
	btn.text(params.text);
	return btn;
}

/* =========================================================================
 * Creates an <input> type control that can be bound to the <body> element.
 *
 * Argument Object [params] parameters:
 *  - id - the id of the <input>
 *  - type - the type of the input item
 *
 * Returns the created control
 * ========================================================================= */
function createInput(params) {
	var input = $("<input/>");
	input.attr("id", params.id);
	input.attr("type", params.type);
	return input;
}

/* =========================================================================
 * Creates a stubbed table without table data - the caller is expected to
 * insert those after this function returns. This function seeks abstractness
 * in how the table headers can be created.
 *
 * Argument Object [params] parameters:
 * - table headers to bind to table
 *
 * Returns the created table
 * ========================================================================= */
function createTable(params) {

	var table = $("<table>");
	table.addClass("table table-condensed table-bordered table-responsive table-hover");
	var thead = $("<thead>");
	var thRow = $("<tr>");

	for (var x = 0; x < params.length; x++) {
		var th = $("<th>");
		th.text(params[x]);
		th.addClass('center-justify');
		thRow.append(th);
	}
	thead.append(thRow);
	table.append(thead);
	table.append($("<tbody>"));
	return table;
}

/* =========================================================================
 * Creates a bread crumb for the study/event/crf/item navigation tables
 *
 * Argument Object [params] parameters:
 * - study - this should always be provided as it is the first crumb
 * - event - the selected event
 * - crf - the selected crf in the event
 *
 * Returns the created bread crumb
 * ========================================================================= */
function createBreadCrumb(params) {

	$(".data > .panel-primary > .panel-body > .space-top > .breadcrumb").remove();

	var ol = $("<ol>");
	ol.addClass("breadcrumb");

	var studyCrumb = $("<li>");
	var studyLink = $("<a>");
	studyLink.text(params.study);
	studyLink.click(function() {
		$("a[href='#studies']").tab('show');
	});

	studyCrumb.append(studyLink);
	if (params.evt) {

		ol.find(".active").removeClass(".active");
		var eventCrumb = $("<li>");
		var eventLink = $("<a>");
		eventLink.text(params.evt);
		eventLink.click(function() {
			$("a[href='#events']").tab('show');
		});

		eventCrumb.addClass("active");
		eventCrumb.append(eventLink);
		ol.append(eventCrumb);
	} else {
		studyCrumb.addClass("active");
	}

	if (params.crf) {

		ol.find(".active").removeClass(".active");
		var crfCrumb = $("<li>");
		var crfLink = $("<a>");
		crfLink.text(params.crf);
		crfLink.click(function() {
			$("a[href='#crfs']").tab('show');
		});
		crfCrumb.addClass("active");
		crfCrumb.append(crfLink);
		ol.append(crfCrumb);
	}	

	if (params.version) {
		ol.find(".active").removeClass(".active");
		var versionCrumb = $("<li>");
		var versionLink = $("<a>");
		versionLink.text(params.version);
		versionLink.click(function() {
			$("a[href='#versions']").tab('show');
		});
		versionCrumb.addClass("active");
		versionCrumb.append(versionLink);
		ol.append(versionCrumb);
	}

	ol.prepend(studyCrumb);
	$(".data > .panel-primary > .panel-body > .space-top").append(ol);
	return ol;
}

/* =========================================================================
 * Resets the expression build surface by deleting all the children
 *
 * Argument Object [parentDiv] parameters:
 * - parentDiv - the expression surface being cleared
 *
 * ========================================================================= */
function resetBuildControls(parentDiv) {
	$("#deleteButton").addClass('hidden');
	parentDiv.children("div").not(".pull-right").remove();
	parentDiv.append(createStartExpressionDroppable());
}

/* =========================================================================
 * Creates the pagination element and manages its subsequent interactions for
 * either studies, events, crfs or items table[s]
 *
 * Argument Object [params] parameters:
 * - div - the div to append the pagination to
 * - itemsArr - the array containing the items to page
 *
 * Returns the created pagination
 * ========================================================================= */
function createPagination(params) {
	params.div.find(".pagination").remove();
	if (params.itemsArr.length > 1) {
		var ul = $("<ul>");
		ul.addClass("pagination");

		var el = $("<li>");
		var aEL = $("<a>");

		var tt = unescape(JSON.parse('"\u00AB\u00AB"'));
		aEL.text(tt);
		aEL.click(function() {
			if (currentPageIndex === 0) {
				resetTable({
					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
				});
			} else if (currentPageIndex > 0) {
				currentPageIndex = currentPageIndex - 1;
				resetTable({
					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
				});
			}
		});
		el.append(aEL);
		ul.prepend(el);

		var pager = $("<li>");
		var pagerLink = $("<a>");
		pagerLink.text(currentPageIndex + " of " + params.itemsArr.length);
		pager.append(pagerLink);
		ul.append(pager);

		var rEl = $("<li>");
		var rAEL = $("<a>");
		var tt = unescape(JSON.parse('"\u00BB\u00BB"'));
		rAEL.text(tt);
		rAEL.click(function() {
			if (currentPageIndex < params.itemsArr.length - 1) {
				currentPageIndex = currentPageIndex + 1;
				resetTable({
					arr: params.itemsArr,
					table: params.div.find("table"),
					pagination: params.div.find(".pagination")
				});
			}
		});
		rEl.append(rAEL);
		ul.append(rEl);
		return ul;
	}
}

/* ============================================================
 * Resets a given table by remove all the existing table rows
 *
 * Argument Object [params] parameters:
 * - table - the table to reset
 * - arr - arr containing the updated items to add to the table
 *
 * ============================================================ */
function resetTable(params) {

	params.table.find("tbody > tr").remove();
	var alphaArr = params.arr[currentPageIndex];
	for (var chunk in alphaArr) {
		var tr = alphaArr[chunk];
		$(tr).find("td").toArray().map(function(x) {
			if (!$(x).is(".ui-draggable, .item-datatype, .item-description")) {
				createDraggable({
					element: $(x)
				});
			}
		});
		params.table.find("tbody").append(tr);
	}
	if (params.pagination) {
		params.pagination.find("a:eq(1)").text(currentPageIndex + 1 + " of " + params.arr.length);
	}	
	$(params.table.parent()).html(params.itemsTable);
}

/* =================================================
 * Creates a drop surface with the dotted borders
 *
 * Argument Object [params] parameters:
 * - id - the id of the drop surface
 * - class - the class[es] for the drop surface
 *
 * Returns the created drop surface
 * ================================================= */
function createDropSurface(params) {
	var div = createDiv({
		id: params.id,
		divClass: "dotted-border init " + params.class

	}).text(params.text);
	createPopover(div);
	return div;	
}

/* ====================================================================================
 * Creates the start expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting left parenthesis and right parenthesis.
 *
 * Returns the created drop surface
 * =================================================================================== */
function createStartExpressionDroppable() {
	var div = createDropSurface({
		class: "group",
		text: messageSource.texts.groupOrData
	});
	createDroppable({
		element: div,
		accept: ".group, .data p, div[id='items'] td"
	});
	return div;
}

/* =========================================================================
 * Creates the symbol expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting math and comparison symbols.
 *
 * Returns the created drop surface
 * ========================================================================= */
function createSymbolDroppable() {
	var div = createDropSurface({
		class: "comp compare",
		text: messageSource.texts.compareOrCalculate
	});
	createDroppable({
		element: div,
		accept: ".compare p"
	});
	return div;
}

/* =============================================================================
 * Creates the right parenthesis drop surface with the dotted borders. The drop
 * surface already has a right parenthesis added and does not accept drops.
 *
 * Returns the created drop surface
 * =========================================================================== */
function createRPARENDiv() {
	return createDropSurface({
		text: ")",
		class: "group bordered"
	});
}

/* =============================================================================
 * Creates the condition expression drop surface with the dotted borders. This
 * drop surface is a group capable of accepting math and comparison symbols.
 *
 * Returns the created drop surface
 * =========================================================================== */
function createConditionDroppable() {
	var div = createDropSurface({
		class: "eval condition",
		text: messageSource.texts.condition
	});
	createDroppable({
		element: div,
		accept: ".condition p"
	});
	return div;
}

/* =============================================================================
 * Creates the drop surface popover to allow editing and deleting the surface
 *
 * Argument Object [droppable] parameters:
 * - droppable - the droppable on which the popover will be bound
 *
 * =========================================================================== */
function createPopover(droppable) {
	var btn = '<div id="edit-pop" type="button" class="pull-left space-right-m" onclick="addDroppable(this)"><span class="glyphicon glyphicon-plus"></span></div><div id="del-pop" class="pull-left space-right-m" type="button" onclick="x(this)"><span class="glyphicon glyphicon-trash"></span></div>';
	droppable.popover({
		html: true,
		content: btn,
		placement: "top",
		trigger: "manual",
		container: droppable
	}).click(function(evt) {
		$(".popover").remove();
		evt.stopPropagation();
		$(this).popover('show');
		if (droppable.attr('item-oid')) {
			showCRFItem(this);
		}
		$(".tooltip").remove();
	}).on('shown.bs.popover', function (x) {
  		$(".tooltip").remove();
  		// Edit tool-tip
  		createToolTip({
  			element: $(this).find("#edit-pop"),
  			title: messageSource.messages.addPopUp
  		});
		$(this).find("#edit-pop").on('show.bs.tooltip', function() {
			$(".tooltip").remove();
		});
  		// Delete tool-tip
  		createToolTip({
  			element: $(this).find("#del-pop"),
  			title: messageSource.messages.removePopUp
  		});
  		$(this).find("#del-pop").on('show.bs.tooltip', function(x) {
			$(".tooltip").remove();
		});
  		// kill event
  		x.stopPropagation();
	});
}

/* =============================================================================
 * Invoked from the drop surface popover, this function creates a valid droppable
 * depending on the current droppable type, the past droppable type and the 
 * preceding droppable type (if any) -
 *
 * Argument Object [popov] parameters:
 * - popov - the pop over which invoked this function
 * =========================================================================== */
function addDroppable(popov) {

	var drop = $(popov).parents(".dotted-border")[0];
	var modalOuterDiv = createDiv({
		divClass: "modal fade tops"
	});

	var modalDialog = createDiv({
		divClass: "modal-dialog"
	});

	var modalContent = createDiv({
		divClass: "modal-content"
	});

	var div = createDiv(Object.create({
		divClass: "modal-body"
	}));

	var groupBtn = createButton({
		text: messageSource.texts.groupData,
		btnClass: "btn btn-success space-right-m"
	}).click(function() {
		modalOuterDiv.remove();
		var d = createStartExpressionDroppable();
		if ($(drop.previousSibling).is(".eval") || !$(drop.previousSibling).is(".dotted-border")) {
			$(drop).before(d);
			createPopover(d);
		} else {
			$(drop).after(d);
			createPopover(d);
		} 
	});

	var compBtn = createButton({
		text: messageSource.texts.compareCalculate,
		btnClass: "btn btn-primary space-right-m"
	}).click(function() {	
		modalOuterDiv.remove();
		var c = createSymbolDroppable();
		if ($(drop).next().size() > 0) {
			if (!$(drop).is(".eval") && !$(drop).is(".comp") && !$(drop).next().is(".comp") && !$(drop).next().is(".eval")) {
				$(drop).after(c);
				createPopover(c);
			}
		} else {
			if (!$(drop).is(".eval") && !$(drop).is(".comp")) {
				$(drop).after(c);
				createPopover(c);
			} else {
				removeInsert(drop, c);
			}
		}
	});

	var evalBtn = createButton({
		text: messageSource.texts.condition,
		btnClass: "btn btn-warning"
	}).click(function() {
		modalOuterDiv.remove();
		var eval = createConditionDroppable();
		if ($(drop).next().size() > 0) {
			if (!$(drop).is(".eval") && !$(drop).is(".comp") && !$(drop).next().is(".comp") && !$(drop).next().is(".eval")) {
				$(drop).after(eval);
				createPopover(eval);
			}
		} else {
			if (!$(drop).is(".eval") && !$(drop).is(".comp")) {
				$(drop).after(eval);
				createPopover(eval);
			} else {
				removeInsert(drop, eval);
			}
		}
	});

	div.append(groupBtn);
	div.append(compBtn);
	div.append(evalBtn);
	div.css("text-align", "center");
	modalContent.append(div);
	modalDialog.append(modalContent);
	modalOuterDiv.append(modalDialog);
	modalOuterDiv.modal({
		backdrop: false
	});
}

function removeInsert(drop, predicate) {
	$(drop).after(predicate);
	$(drop).remove();
}

/* =============================================================================
 * Invoked from the drop surface popover, deletes drop surface from which it was
 * invoked
 *
 * Argument Object [params] parameters:
 * - popov - the pop over which invoked this function
 * =========================================================================== */
function x(popov) {
	$(".tooltip").each(function() {
		$(this).remove();
	});
	if ($("#designSurface").find(".dotted-border").size() > 1) {
		$(popov).parents(".dotted-border").remove();
	}
}

/* ===============================================================
 * Convienience method that creates a tooltip for a given element
 *
 * Argument Object [params] parameters:
 * - element - the element to create the tooltip for
 * - title - the tool tip title
 * =============================================================== */
function createToolTip(params) {
	params.element.tooltip({
		container: "body",
		title: params.title,
		placement: "bottom",
		trigger: "hover focus"
	});
}

/* ================================================================
 * Convienience method that creates an alert. Note that by default,
 * times out after 4 second

 * Argument Object [text] parameters:
 * - text - text alert text
 *
 * * Returns the created alert
 * =============================================================== */
function createAlert(text) {
	var div = createDiv({
		divClass: "alert alert-danger"
	}).text(text);

	var a = $("<a>");
	a.addClass("close");
	a.attr("data-dismiss", "alert");
	a.text("x");
	div.prepend(a);
	setTimeout(function() {
		$(".alert").alert('close');
	}, 5000);
	return div;
}

/* ===========================================================================
 * Handles erratic responses from a server.
 *
 * Arguments [params]:
 * => response - The response from the server (must be a valid http response)
 * ========================================================================== */
function handleErrorResponse(params) {
	removeLoader();
	if (params.response.status === 404) {
		bootbox.alert({
			backdrop: false,
			message: messageSource.messages.serverIsNotAvailable
		});

	} else {
		bootbox.alert({
			backdrop: false,
			message: params.response.responseText
		});
	}
}

/* =========================================================================
 * Creates an element that can be dragged on a specified droppable
 *
 * Argument Object [params] parameters:
 *  - element - the element to convert to a draggable
 *  - target - the intend target[s] for this element
 * ========================================================================= */
function createDraggable(params) {
	$(params.element).draggable({
		scroll: true,
		cursor: "move",
		helper: "clone",
		revert: "invalid",
		snapMode: "outer",
		snap: params.target,
		scrollSensitivity: 100
	});
}

/* ====================================================================
 * Creates an element that can be dropped on with specified draggables
 *
 * Argument Object [params] parameters:
 *  - element - the element to convert to a droppable
 *  - accept - the intend target[s] for this element
 * ==================================================================== */
function createDroppable(params) {
	params.element.droppable({
		accept: params.accept,
		hoverClass: "ui-state-active",
		drop: function(evt, ui) {
			// prevent drop on sorting
			if (ui.draggable.is('.ui-draggable:not(div)')) {
				params.ui = ui;	
				handleDropEvent(params);
			}
		}
	});
	// Handle double click on drop surface
	params.element.dblclick(function() {
		params.element.tooltip("hide");
		params.element.addClass("bordered");
		var input = $("<input>");
		if (parser.isDateValue($(this).text())) {
			input.val($(this).text());
			input.data({date: new Date(input.val())
			}).datepicker('update').children("input").val(new Date(input.val()));
			input.datepicker().on("hide", function() {
				if ($(this).val()) {
					params.element.text($(this).val());
				}
			});
		} else {
			input.val($(this).text());
			input.blur(function() {
				if ($(this).val()) {
					params.element.text($(this).val());
				} else {
					params.element.text(messageSource.texts.dataText);
				}
				$(this).remove();
			});
		}
		input.css({
			"display": "inline",
			"text-align": "center"
		});
		$(this).text("");
		$(this).append(input);
		// focus/select
		input.focus();
		input.select();

		params.element.css('font-weight', 'bold');
	});
	return params.element;
}

function createSortable() {
	placeholder: "ui-state-highlight",
	$(".sortable").sortable({
		start: function(event, ui) {
			$(".tooltip").remove();
			ui.item.addClass("sort");
		},
		stop: function(event, ui) {
			ui.item.removeClass("sort");
		}
	}).disableSelection();
}

function handleDropEvent(params) {
	var existingValue = params.element.val();
	params.element.text("");
	params.element.removeClass("init");
	params.element.addClass("bordered");
	if(params.element.hasClass("invalid")) {
		params.element.removeClass("invalid");
	}
	if (parser.isText(params.ui.draggable)) {
		handleTextDrop(params.element);
	} else if (parser.isDate(params.ui.draggable)) {
		handleDateDrop(params.element);
	} else if (parser.isNumber(params.ui.draggable)) {
		handleNumberDrop(params.element);
	} else if (parser.isEmpty(params.ui.draggable)) {
		params.element.append('""');
	} else if (parser.isCurrentDate(params.ui.draggable)) {
		params.element.append("_CURRENT_DATE");
	} else {
		if (params.ui.draggable.text() == "<") {
			params.element.append("&lt;");
		} else if (params.ui.draggable.text() == ">") {
			params.element.append("&gt;");
		} else if (params.ui.draggable.is("td.group")) {
			params.element.append(params.ui.draggable.attr("item-name"));
		} else {
			params.element.append(params.ui.draggable.text());
		}
	}
	params.element.tooltip("hide");
	// Create the next droppable
	parser.createNextDroppable({
		ui: params.ui,
		element: params.element,
		existingValue: existingValue
	});
	if (params.ui.draggable.is("td.group")) {
		if (params.element.is('.target')) {
			var parent = params.element.parent();
			parent.find('.linefy').val('');
			parent.find('.linefy').addClass('hidden');
			parent.find('.eventify').prop('checked', false);
			parent.find('.versionify').prop('checked', false);
		}
		// Persist attrinutes
		params.element.attr("item-oid", params.ui.draggable.attr("oid"));
		params.element.attr("crf-oid", params.ui.draggable.attr("crf-oid"));
		params.element.attr("event-oid", params.ui.draggable.attr("event-oid"));
		params.element.attr("group-oid", params.ui.draggable.attr("group-oid"));
		params.element.attr("study-oid", params.ui.draggable.attr("study-oid"));
		params.element.attr("version-oid", params.ui.draggable.attr("version-oid"));
	}
	$("#deleteButton").removeClass("hidden");
	params.element.css('font-weight', 'bold');
	if (!$(".sortable").is(".ui-sortable")) {
		createSortable(params.element);
	}
}

/* ======================================================================
 * Handles the dropping of text based UI items. Note that this function
 * enables the dropped item to switch between input and surface on
 * double click
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ==================================================================== */
function handleTextDrop(element) {

	var newInput = $("<input>");
	newInput.attr("type", "text");
	newInput.addClass("input-sm");
	newInput.blur(function() {
		if ($(this).val()) {
			element.text('"' + $(this).val() + '"');
		} else {
			element.text('"' + messageSource.texts.dataText + '"');
		}
	});

	element.append(newInput);
	newInput.focus();
}

/* =============================================================================
 * Handles the dropping of number based UI items. Note that this function
 * enables the dropped item to switch between input (that accepts numbers only) 
 * and surface on double click
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ============================================================================ */
function handleNumberDrop(element) {

	var newInput = $("<input>");
	newInput.attr("type", "number");
	newInput.addClass("input-sm");
	newInput.blur(function() {
		if ($(this).val() && /[0-9]|\./.test($(this).val())) {
			element.text($(this).val());
		} else {
			element.text(messageSource.texts.numberText);
			$("#designSurface").find(".panel-body").prepend(createAlert(messageSource.messages.enterNumber));
		}
	});

	element.append(newInput);
	newInput.focus();
}

/* =============================================================================
 * Handles the dropping of date based UI items. Note that this function
 * enables the dropped item to switch between pop a date picker and surface to 
 * hold the selected date.
 *
 * Note that this function should only be called on drop event.
 *
 * Argument Object [element] parameters:
 *  - element - the element that has been dropped
 * ============================================================================ */
function handleDateDrop(element) {
	var newInput = $("<input>");
	newInput.addClass("input-sm");
	$.fn.datepicker.defaults.format = getCookie('bootstrapDateFormat');
	newInput.datepicker().on("hide", function() {
		if ($(this).val()) {
			element.text($(this).val());
		} else {
			element.text(messageSource.texts.selectDate);
		}
	});
	element.append(newInput);
	newInput.focus();
}

/* ===================================================
 * Adds the studies to the studies table for display.
 *
 *
 * Argument Object [studies] parameters:
 *  - studies - the return studies from CC
 * =================================================== */
function loadStudies(studies) {
	var itemArr = [];
	var $studies = $("div[id='studies']");
	$studies.find("table").remove();
	if (studies) {
		// Table headers
		var table = createTable([messageSource.texts.nameText, messageSource.texts.identifier]);
		for (var x = 0; x < studies.length; x++) {
			var study = studies[x];
			var tr = $("<tr>");
			tr.attr("id", x);
			tr.click(function() {
				var row = this;
				var data = JSON.parse(sessionStorage.getItem("studies"));
				// Extract selected study
				var currentStudy = data[$(row).attr("id")];
				if (parser.getStudy() !== data[$(row).attr("id")].id && 
					($("div[id='studies']").find("table > tbody > tr").size() > 1 && $(".dotted-border").size() > 2) && !editing) {
					var target = $("input.target").first();
					createPrompt({
						row: row,
						study: currentStudy,
						target: target
					});
				} else {
					resetStudy({
						row: row,
						study: currentStudy,
						click: false
					});
				}
				editing = false;
			});

			var tdName = $("<td>");
			tdName.text(study.name);
			tdName.attr("oid", study.oid);

			var tdIdentifier = $("<td>");
			tdIdentifier.text(study.identifier);

			tr.append(tdName);
			tr.append(tdIdentifier);
			itemArr.push(tr);
		}

		$studies.append(table);
		currentPageIndex = 0;

		// Global
		var chunkedItemsArr = itemArr.chunk(10);
		var pagination = createPagination({
			itemsArr: chunkedItemsArr,
			div: $studies
		});

		table.after(pagination);
		resetTable({
			table: table,
			arr: chunkedItemsArr,
			pagination: pagination
		});

		// probably editing
		if (parser.getStudy()) {
			var st = studies.filter(function(x) {
				return x.id === parser.getStudy();
			});
			$('tr[id="'+ studies.indexOf(st[0]) +'"]').click();
		} else {
			$(".table-hover").find("tbody > tr").filter(":first").click();
		}
		// Initial load should show studies
		$("a[href='#studies']").tab('show');
	}
}

/* =================================================================
 * Adds the a given study's events to the events table for display.
 *
 * Argument Object [study] parameters:
 *  - study - the study for whom events should be loaded
 * ============================================================== */
function loadStudyEvents(studyId) {
	$("div[id='events']").find("table").remove();
	// Get study from sessionStorage.
	var study = parser.extractStudy(studyId);
	if (study.events != undefined) {
		processStudyEvents(study);
	} else {
		$("body").append(createLoader());
		var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
		$.ajax({
			type: "POST",
			url: c + "/rules?action=events&id=" + studyId,
			success: function(response) {
				var events = response;
				// FF can return a string
				if (typeof(response) === "string") {
					events = JSON.parse(response);
				}
				study.events = events;
				// Save study to the session storage, in order to use it in future instead uploading events again.
				parser.addEventsToStudy(study.id, events);
				processStudyEvents(study);
				removeLoader();
			},
			error: function(response) {
				handleErrorResponse({
					response: response
				});
			}
		});
	}
}

function processStudyEvents(study) {
	var itemArr = [];
	var eventTable = createTable([messageSource.texts.nameText, messageSource.texts.description]);
	for (var x = 0; x < study.events.length; x++) {
		var studyEvent = study.events[x];
		var tr = $("<tr>");
		tr.attr("id", x);
		tr.click(function() {
			$("a[href='#crfs']").tab('show');
			// Make bold
			$(this).siblings(".selected").removeClass("selected");
			$(this).addClass("selected");

			var currentEvent = study.events[$(this).attr("id")];
			loadEventCRFs({
				studyId: study.id,
				eventId: currentEvent.id
			});

			createBreadCrumb({
				study: study.name,
				evt: currentEvent.name
			});
		});

		var tdName = $("<td>");
		tdName.text(studyEvent.name);
		tdName.attr("oid", studyEvent.oid);

		var tdDescription = $("<td>");
		if (studyEvent.description) {
			if (studyEvent.description.length > 25) {
				tdDescription.text(studyEvent.description.slice(0, 20) + "...");
				tdDescription.tooltip({
					placement: "top",
					container: "body",
					title: studyEvent.description
				});

			} else {
				tdDescription.text(studyEvent.description);
			}
		}
		tr.append(tdName);
		tr.append(tdDescription);
		itemArr.push(tr);
	}

	var $events = $("div[id='events']");
	$events.append(eventTable);
	currentPageIndex = 0;
	// Global
	var chunkedItemsArr = itemArr.chunk(10);
	var pagination = createPagination({
		div: $events,
		itemsArr: chunkedItemsArr
	});

	eventTable.after(pagination);
	resetTable({
		table: eventTable,
		arr: chunkedItemsArr,
		pagination: pagination
	});
}

/* =================================================================
 * Adds the a given event's crfs to the crf table for display.
 *
 * Argument Object [params] parameters:
 * - studyEvent - the event for whom crfs should be loaded
 * - study - the study to which the event belongs to
 * ============================================================== */
function loadEventCRFs(params) {
	var eventId = params.eventId;
	var studyId = params.studyId;

	var crfsDiv = $("div[id='crfs']");
	crfsDiv.find("table").remove();
	if (params.eventId && studyId) {
		var event = parser.extractStudyEvent(studyId, eventId);
		var study = parser.extractStudy(studyId);
		if (event.crfs != undefined) {
			processEventCRFs({
				study: study,
				event: event,
				crfs: event.crfs
			});
		} else {
			$("body").append(createLoader());
			var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
			$.ajax({
				type: "POST",
				url: c + "/rules?action=crfs&id=" + eventId,
				success: function(response) {
					var crfs = response;
					// FF can return a string
					if (typeof(response) === "string") {
						crfs = JSON.parse(response);
					}
					parser.addCRFsToEvent(studyId, eventId, crfs);

					processEventCRFs({
						study: study,
						event: event,
						crfs: crfs
					});
					removeLoader();
				},
				error: function(response) {
					handleErrorResponse({
						response: response
					});
				}
			});
		}
	}
}

function processEventCRFs(params) {
	var crfs = params.crfs;
	var itemArr = [];
	var crfTable = createTable([messageSource.texts.nameText, messageSource.texts.description]);
	for (var cf = 0; cf < crfs.length; cf++) {
		var crf = crfs[cf];
		var tr = $("<tr>");
		tr.attr("id", cf);
		tr.click(function () {
			$("a[href='#versions']").tab('show');
			// Make bold
			$(this).siblings(".selected").removeClass("selected");
			$(this).addClass("selected");
			var currentCRF = crfs[$(this).attr("id")];
			loadCRFVersions({
				crf: currentCRF,
				study: params.study,
				event: params.event
			});
			createBreadCrumb({
				crf: currentCRF.name,
				study: params.study.name,
				evt: params.event.name
			});
		});

		var tdName = $("<td>");
		tdName.text(crf.name);
		tdName.attr("oid", crf.oid);
		var tdDescription = $("<td>");
		if (crf.description) {
			if (crf.description.length > 25) {
				tdDescription.text(crf.description.slice(0, 20) + "...");
				tdDescription.tooltip({
					placement: "top",
					container: "body",
					title: crf.description
				});
			} else {
				tdDescription.text(crf.description);
			}
		}
		tr.append(tdName);
		tr.append(tdDescription);
		itemArr.push(tr);
	}

	var $crfsDiv = $("div[id='crfs']");
	$crfsDiv.append(crfTable);
	currentPageIndex = 0;

	// Global
	var chunkedItemsArr = itemArr.chunk(10);
	var pagination = createPagination({
		div: $crfsDiv,
		itemsArr: chunkedItemsArr
	});

	crfTable.after(pagination);
	resetTable({
		table: crfTable,
		arr: chunkedItemsArr,
		pagination: pagination
	});
}

/* =================================================================
 * Adds the a given crf's versions to the table for display.
 *
 * Argument Object [params] parameters:
 * - crf - the crf for whom versions should be loaded
 * - evt - the event to which the crf belongs to
 * - study - the study to which the event belongs to
 * ============================================================== */
function loadCRFVersions(params) {
	var versionsDiv = $("div[id='versions']");
	versionsDiv.find("table").remove();
	var studyId = params.study.id;
	var eventId = params.event.id;
	var crfId = params.crf.id;
	var crf = parser.extractEventCRF(studyId, eventId, crfId);
	if (crf.versions != undefined) {
		processCRFVersions(params)
	} else {
		$("body").append(createLoader());
		var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
		$.ajax({
			type: "POST",
			url: c + "/rules?action=versions&id=" + params.crf.id,
			success: function(response) {
				var versions = response;
				// FF can return a string
				if (typeof(response) === "string") {
					versions = JSON.parse(response);
				}
				params.crf.versions = versions;
				parser.addVersionsToCRF(studyId, eventId, crfId, versions);
				processCRFVersions(params);
				removeLoader();
			},
			error: function(response) {
				handleErrorResponse({
					response: response
				});
			}
		});
	}
}

function processCRFVersions(params) {
	var versionsDiv = $("div[id='versions']");
	var itemArr = [];
	var versionTable = createTable([messageSource.texts.nameText]);
	for (var ver = 0; ver < params.crf.versions.length; ver++) {
		var version = params.crf.versions[ver];
		var tr = $("<tr>");
		tr.attr("id", ver);
		tr.click(function() {
			$("a[href='#items']").tab('show');
			// Make bold
			$(this).siblings(".selected").removeClass("selected");
			$(this).addClass("selected");
			var currentVersion = params.crf.versions[$(this).attr("id")];
			loadCRFVersionItems({
				crf: params.crf,
				event: params.event,
				version: currentVersion,
				study: params.study
			});
			createBreadCrumb({
				crf: params.crf.name,
				evt: params.event.name,
				study: params.study.name,
				version: currentVersion.name
			});
		});

		var tdName = $("<td>");
		tdName.text(version.name);
		tdName.attr("oid", version.oid);

		tr.append(tdName);
		itemArr.push(tr);
	}

	versionsDiv.append(versionTable);
	currentPageIndex = 0;
	var chunkedItemsArr = itemArr.chunk(10);
	var pagination = createPagination({
		div: versionsDiv,
		itemsArr: chunkedItemsArr
	});

	versionTable.after(pagination);
	resetTable({
		table: versionTable,
		arr: chunkedItemsArr,
		pagination: pagination
	});
}
/* =================================================================
 * Adds the a given crf's items to the items table for display.
 *
 *
 * Argument Object [params] parameters:
 * - crf - the crf for whom items should be loaded
 * ============================================================== */
function loadCRFVersionItems(params) {
	var itemsDiv = $("div[id='items']");
	itemsDiv.find("table").remove();
	if (params.version) {
		var version = parser.extractCRFVersion(params.study.id, params.event.id, params.crf.id, params.version.id);
		if (params.version.items != undefined) {
			processCRFVersionItems(params);
		} else {
			$("body").append(createLoader());
			var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
			$.ajax({
				type: "POST",
				url: c + "/rules?action=items&id=" + params.version.id + "&studyId=" + params.study.id,
				success: function(response) {
					var items = response;
					// FF can return a string
					if (typeof(response) === "string") {
						items = JSON.parse(response);
					}
					params.version.items = items;
					parser.addItemsToVersion(params.study.id, params.event.id, params.crf.id, params.version.id, items);
					processCRFVersionItems(params);
					removeLoader();
				},
				error: function(response) {
					handleErrorResponse({
						response: response
					});
				}
			});
		}
	}
}

function processCRFVersionItems(params) {
	var itemArr = [];
	var itemsDiv = $("div[id='items']");
	var itemsTable = createTable([messageSource.texts.nameText,
		messageSource.texts.description,
		messageSource.texts.dataType]);
	for (var it = 0; it < params.version.items.length; it++) {
		var item = params.version.items[it];
		var tr = $("<tr>");
		var tdName = $("<td>");
		if (item.name.length > 25) {
			tdName.text(item.description.slice(0, 20) + "...");
			tdName.tooltip({
				placement: "top",
				container: "body",
				title: item.name
			});
		} else {
			tdName.text(item.name);
		}
		tdName.click(function() {
			handleClickDrop(this);
		});
		tdName.text(item.name);
		tdName.addClass("group");
		tdName.attr("item-name", item.name);
		// Attributes
		tdName.attr("oid", item.oid);
		tdName.attr("crf-oid", params.crf.oid);
		tdName.attr("event-oid", params.event.oid);
		tdName.attr("group-oid", item.group);
		tdName.attr("study-oid", params.study.oid);
		tdName.attr("version-oid", params.version.oid);
		createDraggable({
			element: tdName,
			target: ($("#dataSurface"), $("#secondDataSurface"))
		});

		var tdDescription = $("<td class='item-description'>");
		if (item.description) {
			if (item.description.length > 25) {
				tdDescription.text(item.description.slice(0, 20) + "...");
				tdDescription.tooltip({
					placement: "top",
					container: "body",
					title: item.description
				});

			} else {
				tdDescription.text(item.description);
			}
		}

		var tdDataType = $("<td class='item-datatype'>");
		tdDataType.text(mapItemType(item.type));
		tr.append(tdName);
		tr.append(tdDescription);
		tr.append(tdDataType);
		itemArr.push(tr);
	}

	itemsDiv.append(itemsTable);
	currentPageIndex = 0;

	// Global
	var chunkedItemsArr = itemArr.chunk(10);
	var pagination = createPagination({
		div: itemsDiv,
		itemsArr: chunkedItemsArr
	});

	itemsTable.after(pagination);
	resetTable({
		table: itemsTable,
		arr: chunkedItemsArr,
		pagination: pagination
	});
}

function createLoader() {
	
	$("a[id='exit']").attr("href","#");
	var modalOuterDiv = createDiv({
		divClass: "spinner"
	});
	modalOuterDiv.append($('<img src="images/loader.gif" alt="Loading...">'));
	return modalOuterDiv;
}

function removeLoader() {

	$(".spinner").remove();
	var c = new RegExp('(.+?(?=/))').exec(window.location.pathname)[0];
	$("a[id='exit']").attr("href", c + "/ViewRuleAssignment");
}

function createPrompt(params) {
	bootbox.dialog({
		message: messageSource.messages.expressionWillBeLost,
		title: messageSource.texts.changingStudy,
		buttons: {
			clear: {
				label: messageSource.texts.clear,
				className: "btn-danger",
				callback: function() {
					params.reset = true;
					resetStudy(params);
				}
			},
			main: {
				label: messageSource.texts.cancel,
				className: "btn-primary",
				callback: function() {
					bootbox.hideAll();
				}
			}
		}
	});
}

function resetStudy(params) {
	$("a[href='#events']").tab('show');
	// Make bold
	$(params.row).siblings(".selected").removeClass("selected");
	$(params.row).addClass("selected");
	parser.setStudy(params.study.id);
	loadStudyEvents(params.study.id);

	// boot-strap crumb
	createBreadCrumb({
		study: params.study.name
	});
	if (params.reset) {
		resetBuildControls($("#designSurface > .panel > .panel-body").filter(":first"));
	}
	if (params.changeStudy) {
		// Reset the parameters
		$(".dotted-border:not(:empty), .target:not(:empty)").each(function() {
			var item = parser.findStudyItem({
				study: params.study,
				name: $(this).text()
			});
            if (item) {
                $(this).attr('event-oid', item.eventOid);
                $(this).attr('study-oid', params.study.oid);
                parser.resetTarget({
                    evt: item.eventOid,
                    oid: $(params.target).attr('item-oid')
                });
            }
		});
	}
}
function showCRFItem(ele) {

	// validate presence of all attributest
	if ($(ele).attr("study-oid") === undefined || $(ele).attr("event-oid") === undefined
		|| $(ele).attr("crf-oid") === undefined || $(ele).attr("version-oid") === undefined
		|| $(ele).attr("item-oid") === undefined) {
		return;
	}

	parser.recursiveSelect({
		click: true,
		type: "studies",
		candidate: $(ele).attr("study-oid")
	});
	parser.recursiveSelect({
		click: true,
		type: "events",
		candidate: $(ele).attr("event-oid")
	});
	// bold crf
	parser.recursiveSelect({
		click: true,
		type: "crfs",
		candidate: $(ele).attr("crf-oid")
	});
	// bold crf version
	parser.recursiveSelect({
		click: true,
		type: "versions",
		candidate: $(ele).attr("version-oid")
	});
	parser.recursiveSelect({
		click: true,
		type: "items",
		candidate: $(ele).attr("item-oid")
	});
}

function mapItemType(type) {
	var result = "";
	switch (type) {
		case 'st':
			result = messageSource.dataType.st;
			break;
		case 'int':
			result = messageSource.dataType.int;
			break;
		case 'real':
			result = messageSource.dataType.real;
			break;
		case 'date':
			result = messageSource.dataType.date;
			break;
		case 'pdate':
			result = messageSource.dataType.pdate;
			break;
		case 'file':
			result = messageSource.dataType.file;
			break;
		case 'code':
            result = messageSource.dataType.code;
            break;
		default:
			result = type;
			break;
	}
	return result;
}

function handleClickDrop(ele) {
	var params = Object.create(null);
	// Parameters for drop
	params.ui = Object.create(null);
	params.ui.draggable = $(ele);
	// Get a matching drop surface, if more than one, select last
	if ($(ele).is('.group')) {
		params.element = getDropSurfaceElement(ele);
	} else if ($(ele).is('.compare')) {
		params.element = $('.dotted-border.' + 'compare').last();
	} else if ($(ele).is('.condition')) {
		params.element = $('.dotted-border.' + 'condition').last();
	}
	// Do not overwrite by click
	if (!params.element.is('.bordered')) {
		handleDropEvent(params);
	}
}

function getDropSurfaceElement(clickedElement) {
	//If left parenthesis is clicked
	var $group = $('.dotted-border.group');
	if($(clickedElement).is('.leftPAREN')){
		//If there's more than one element and Group or Data is left most surface
		if($group.length > 1 && $group.first().text() == messageSource.texts.groupOrData) {
			return $group.first();
		} else {
			return $group.last();
		}
	}	
	else if ($group.last().text() == ')') {
		return $group.eq(-2);
	} else {
		return $group.last();
	}
}