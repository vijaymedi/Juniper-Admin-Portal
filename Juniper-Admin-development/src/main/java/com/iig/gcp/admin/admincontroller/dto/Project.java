package com.iig.gcp.admin.admincontroller.dto;

public class Project {

	private int project_sequence ;
	private String project_id;
	private String project_name;
	private String created_by;
	private String created_date;  
	private String updated_by;
	private String updated_date;     
	private String project_owner;
	private String project_description;
	private String gsd_group;
	private String gsd_key;
	
	public int getProject_sequence() {
		return project_sequence;
	}
	public void setProject_sequence(int project_sequence) {
		this.project_sequence = project_sequence;
	}
	public String getProject_id() {
		return project_id;
	}
	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}
	public String getProject_name() {
		return project_name;
	}
	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}
	public String getProject_owner() {
		return project_owner;
	}
	public void setProject_owner(String project_owner) {
		this.project_owner = project_owner;
	}
	public String getProject_description() {
		return project_description;
	}
	public void setProject_description(String project_description) {
		this.project_description = project_description;
	}
	public String getGsd_group() {
		return gsd_group;
	}
	public void setGsd_group(String gsd_group) {
		this.gsd_group = gsd_group;
	}
	public String getGsd_key() {
		return gsd_key;
	}
	public void setGsd_key(String gsd_key) {
		this.gsd_key = gsd_key;
	}
}
