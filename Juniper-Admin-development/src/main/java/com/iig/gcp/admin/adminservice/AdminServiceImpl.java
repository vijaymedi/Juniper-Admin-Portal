package com.iig.gcp.admin.adminservice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iig.gcp.admin.admincontroller.dao.*;
import com.iig.gcp.admin.admincontroller.dto.*;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	AdminDAO admindao;
	
	@Override
	public String getUser(String user) throws Exception {
		return admindao.getUser(user);
	}
	
	@Override
	public ArrayList<String> getAllUsers() throws Exception {
		return admindao.getAllUsers();
	}

	@Override
	public String  onBoardUser(@Valid String x, @Valid boolean isAdmin, @Valid String project,  HttpServletRequest request) throws Exception {
		JSONObject jsonObject=null;
		try {
		jsonObject= new JSONObject(x);
		}catch(JSONException e) {
			throw new Exception("Please Select User ID for Onboarding");
		}
		String group_seq=jsonObject.getString("group");
		String username=jsonObject.getString("username");
		 return admindao.adminupdusrgrp(username,group_seq,project);		
		
	}
	
	@Override
	public String registerProject(@Valid String projectId, String projectName, String projectOwner,
			String projectDetails, String gsdGroup,String gsdKey,int userSequence) throws ClassNotFoundException, Exception {
		return admindao.registerProject(projectId,projectName,projectOwner,projectDetails,gsdGroup,gsdKey,userSequence);
	}

	@Override
	public Project getProject(@Valid String projectId) throws Exception{
		return admindao.getProject(projectId);
	}

	@Override
	public String registerAddAdminAccess(int projectSeq, int user_sequence) throws Exception {
		return admindao.registerAddAdminAccess(projectSeq,user_sequence);
	}

	@Override
	public ArrayList<Feature> getFeatures(String userid,String project) throws Exception {
		
		return admindao.getFeatures(userid,project);
	}

	@Override
	public ArrayList<Feature> getFeaturesAlready(String userid,String project) throws Exception {
		
		return admindao.getFeaturesAlready(userid,project);
	}

	@Override
	public int getUserSequence(String userid) throws Exception {
		
		return admindao.getUserSequence(userid);
	}
	
	@Override
	public ArrayList<Group> getGroups(String user, String project) throws Exception {
		
		return admindao.getGroups(user,project);
	}
	
	@Override
	public ArrayList<Group> getGroupsAlready(String user, String project) throws Exception {
		
		return admindao.getGroupsAlready(user,project);
	}
	
	
	@Override
	public List<String> getProjects() throws Exception {
		return admindao.getProjects();
	}

	@Override
	public Project fetchProjectDetails(@Valid String projectId) throws Exception {
		return admindao.fetchProjectDetails(projectId);
	}

	@Override
	public String updateProject(@Valid String projectId, String projectName, String projectOwner, String projectDescription,String gsdGroup,String gsdKey,
			String user_id) throws Exception {
		return admindao.updateProject(projectId,projectName,projectOwner,projectDescription,gsdGroup,gsdKey);
	}

/*
 // Commenting this code as of now, can be used if needed in future
	@Override
	public String deleteProject(@Valid String projectId) throws Exception {
		return admindao.deleteProject(projectId);
		}*/

	@Override
	public String registerUserInSystem(@Valid String userId,  String domain,String onboardedUserFullName, String userEmail,
			String loggedInUser) throws Exception {
		return admindao.registerUser(userId,domain,onboardedUserFullName,userEmail,loggedInUser);	
	}

	@Override
	public String updateUser(@Valid String userId, String domain, String onboardedUserFullName,String userEmail, String loggedInUser) throws Exception {
		return admindao.updateUser(userId,domain,onboardedUserFullName,userEmail,loggedInUser);	
	}

	@Override
	public List<String> fetchUserIds() throws Exception {
		return admindao.fetchUserIds();
	}

	@Override
	public UserAccount fetchUserDetails(String userId) throws Exception {
		return admindao.fetchUserAttributes(userId);	
	}

	@Override
	public String linkUserGroupsToNewProject(int userSequence,Project project) throws Exception {
		return admindao.linkUserGroupsToProject(userSequence,project);
	}

	@Override
	public int checkPSID(@Valid String psid) throws Exception {
		return admindao.validatePSID(psid);
	}
	
	@Override
	public ArrayList<Project> getProjects1(String username) throws Exception{
		return admindao.getProjects1(username);
	}

	@Override
	public List<CountryMaster> fetchCountries(@Valid String region) throws Exception {
		return admindao.fetchCountries(region);
	}

	@Override
	public int checkEIM(@Valid String system_eim) throws Exception {
		return admindao.checkEIM(system_eim);
	}

	@Override
	public String registerSystem(@Valid String systemEIM, String systemName, String region, String country,
			String environmentType, String userName) throws ClassNotFoundException, SQLException, Exception {
		return admindao.registerSystem(systemEIM, systemName, region, country, environmentType, userName);
	}



}
