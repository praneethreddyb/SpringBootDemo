package com.demo.core.util;

import java.util.List;

public class JqGridData {

	/** Total number of records */
	private String records;
	
	private boolean select;

	/** Total number of pages */
	private int total; // rows

	/** The current page number */
	private int page;

	/** The actual data */
	private Object rows;
	private Object zc_extra;

	/** The pivotData data */
	private Object pivotData;

	private Object aggregation;

	/** no of rec per page */
	private int size;

	public JqGridData() {

	}
	//, Object rows, Object pivotData, Object aggregation
	public JqGridData(int total, int page, int size,ZcMap data, int records,boolean select) {
		
		this(total, page, size, data, records);
		this.select = select;
	}
	
	public JqGridData(int total, int page, int size,ZcMap data, int records) {
		this.total = total;
		this.page = page + 1;
		this.rows = data.get("rows");
		this.pivotData = data.get("pivotData");
		this.aggregation = data.get("aggregation");
		this.zc_extra = data.get("zc_extra");
		this.size = size;
		this.records = records + "";
	}
	
	public int getTotal() {
		return total;
	}

	public int getPage() {
		return page;
	}

	public Object getRows() {
		return rows;
	}

	public Object getPivotData() {
		return pivotData;
	}

	public Object getAggregation() {
		return aggregation;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(List<ZcMap> rows) {
		this.rows = rows;
	}

	public void setPivotData(List<ZcMap> pivotData) {
		this.pivotData = pivotData;
	}

	public void setAggregation(ZcMap aggregation) {
		this.aggregation = aggregation;
	}

	public int getSize() {
		return size;
	}

	public String getRecords() {
		return records;
	}

	public void setRecords(String records) {
		this.records = records;
	}

	public void setSize(int size) {
		this.size = size;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	public Object getZc_extra() {
		return zc_extra;
	}
	public void setZc_extra(Object zc_extra) {
		this.zc_extra = zc_extra;
	}

}
