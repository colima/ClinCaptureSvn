/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

/* 
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).
 * For details see: http://www.openclinica.org/license
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.logic.expressionTree;

import org.akaza.openclinica.exception.OpenClinicaSystemException;

public class RelationalOpNode extends ExpressionNode {
	Operator op; // The operator.
	ExpressionNode left; // The expression for its left operand.
	ExpressionNode right; // The expression for its right operand.

	RelationalOpNode(Operator op, ExpressionNode left, ExpressionNode right) {
		// Construct a BinOpNode containing the specified data.
		assert op == Operator.GREATER_THAN || op == Operator.GREATER_THAN_EQUAL
				|| op == Operator.LESS_THAN || op == Operator.LESS_THAN_EQUAL;
		assert left != null && right != null;
		this.op = op;
		this.left = left;
		this.right = right;
	}

	@Override
	String testCalculate() throws OpenClinicaSystemException {
		double x, y;
		String l = String.valueOf(left.testValue());
		String r = String.valueOf(right.testValue());
		if (blankAgainstDateyyyyMMdd(l, r)) {
			return "blankAgainstDateyyyyMMdd";
		}
		validate(l, r, left.getNumber(), right.getNumber());
		if (ExpressionTreeHelper.isDateyyyyMMdd(l)
				&& ExpressionTreeHelper.isDateyyyyMMdd(r)) {
			x = ExpressionTreeHelper.getDate(l).getTime();
			y = ExpressionTreeHelper.getDate(r).getTime();
		} else {
			x = Double.valueOf(l);
			y = Double.valueOf(r);
		}
		return calc(x, y);
	}

	@Override
	String calculate() throws OpenClinicaSystemException {
		double x, y;
		String l = String.valueOf(left.value());
		String r = String.valueOf(right.value());

		validate(l, r);
		
		if (ExpressionTreeHelper.isDateyyyyMMdd(l)
				&& ExpressionTreeHelper.isDateyyyyMMdd(r)) {
			x = ExpressionTreeHelper.getDate(l).getTime();
			y = ExpressionTreeHelper.getDate(r).getTime();
		} else {
			x = Double.valueOf(l);
			y = Double.valueOf(r);
		}
		return calc(x, y);
	}

	private String calc(double x, double y) throws OpenClinicaSystemException {
		switch (op) {
		case GREATER_THAN:
			return String.valueOf(x > y);
		case GREATER_THAN_EQUAL:
			return String.valueOf(x >= y);
		case LESS_THAN:
			return String.valueOf(x < y);
		case LESS_THAN_EQUAL:
			return String.valueOf(x <= y);
		default:
			return null; // Bad operator!
		}
	}

	private boolean isDouble(String x, String y) {
		try {
			Double.valueOf(x);
			Double.valueOf(y);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	void validate(String l, String r) throws OpenClinicaSystemException {
		if (!(ExpressionTreeHelper.isDateyyyyMMdd(l) && ExpressionTreeHelper
				.isDateyyyyMMdd(r)) && !isDouble(l, r)) {
			throw new OpenClinicaSystemException("OCRERR_0001", new Object[] {
					l, r, op.toString() });
		}
	}

	void validate(String l, String r, String ltext, String rtext)
			throws OpenClinicaSystemException {
		if (!(ExpressionTreeHelper.isDateyyyyMMdd(l) && ExpressionTreeHelper
				.isDateyyyyMMdd(r)) && !isDouble(l, r)) {
			throw new OpenClinicaSystemException("OCRERR_0001", new Object[] {
					ltext, rtext, op.toString() });
		}
	}

	private boolean blankAgainstDateyyyyMMdd(String l, String r) {
		return l.isEmpty() && ExpressionTreeHelper.isDateyyyyMMdd(r)
				|| r.isEmpty() && ExpressionTreeHelper.isDateyyyyMMdd(l);
	}

	@Override
	void printStackCommands() {
		// To evalute the expression on a stack machine, first do
		// whatever is necessary to evaluate the left operand, leaving
		// the answer on the stack. Then do the same thing for the
		// second operand. Then apply the operator (which means popping
		// the operands, applying the operator, and pushing the result).
		left.printStackCommands();
		right.printStackCommands();
		logger.info("  Operator " + op);
	}
}
