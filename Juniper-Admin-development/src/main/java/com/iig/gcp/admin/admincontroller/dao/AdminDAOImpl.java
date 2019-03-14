package com.iig.gcp.admin.admincontroller.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;
import com.iig.gcp.admin.admincontroller.dto.*;

import com.iig.gcp.admin.admincontroller.utils.*;


@Component
public class AdminDAOImpl implements AdminDAO {

	@Autowired
	private ConnectionUtils ConnectionUtils;
	
	private static String SPACE = " ";
	private static String COMMA = ",";
	private static String QUOTE = "\'";
	private static String SEMICOLON = ";";
	private static String PROJECT_MASTER_TABLE = "JUNIPER_PROJECT_MASTER";
	private static String PROJECT_LINK_TABLE = "JUNIPER_PRO_U_FEAT_LINK";
	private static String USER_MASTER_TABLE = "JUNIPER_USER_MASTER";
	private static String USER_GROUP_MASTER_TABLE = "JUNIPER_USER_GROUP_MASTER";
	private static String GROUP_MASTER_TABLE = "JUNIPER_GROUP_MASTER";
	private static String UGROUP_GROUP_TABLE="JUNIPER_UGROUP_USER_LINK";
	
	private static String SYSTEM_MASTER_TABLE = "JUNIPER_SYSTEM_MASTER";
	private static String COUNTRY_REGION_MAPPING_TABLE = "JUNIPER_REGION_COUNTRY_MASTER";
	

	//User_Group_Account_Constants
	private static String JUNIPER_JADMIN_GROUP = "JUNIPER_JADMIN";
	
	

	
	@Override
	public String adminupdusrgrp(String username, String group_seq, String project) throws Exception {
		Connection connection = ConnectionUtils.getConnection();
		int user_seq=0,proj_seq=0;
		String[] group_int = group_seq.split(",");
		String message="";
		PreparedStatement pstm = connection.prepareStatement("select user_sequence from juniper_user_master where user_id='" + username + "'");
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			user_seq = rs.getInt(1);
		}
		if(!group_seq.isEmpty()){
			pstm = connection.prepareStatement("select PROJECT_SEQUENCE from juniper_user_group_master where USER_GROUP_SEQUENCE='" + Integer.parseInt(group_int[0]) + "'");
			ResultSet rs2 = pstm.executeQuery();
			while (rs2.next()) 
			{
				proj_seq = rs2.getInt(1);
			}		
		}
		String pstm3;
		if(proj_seq==0){
			pstm3 = "delete from juniper_ugroup_user_link where USER_SEQUENCE=" + user_seq + " and project_sequence in (select project_sequence from JUNIPER_PROJECT_MASTER where project_id ='"+project+"')";
			Statement statement1 = connection.createStatement();
			statement1.executeUpdate(pstm3);
			message= ""+username+" revoked from group successfully";
		}else {
			pstm3 = "delete from juniper_ugroup_user_link where USER_SEQUENCE=" + user_seq + " and project_sequence="+ proj_seq;
			Statement statement1 = connection.createStatement();
			statement1.executeUpdate(pstm3);
			int i = 0;
			String pstm1;
			while (i < group_int.length) {
				pstm1 = "insert into JUNIPER_UGROUP_USER_LINK (USER_GROUP_SEQUENCE,USER_SEQUENCE,PROJECT_SEQUENCE)" + " values ("+Integer.parseInt(group_int[i])+
						","+user_seq+","+proj_seq+")";
				Statement statement = connection.createStatement();
				statement.executeUpdate(pstm1);
				i++;
				message= ""+username+" mapped with group successfully";
			}
		}
		pstm.close();
		connection.close();
		
		return message;
	}

	@Override
	public ArrayList<Group> getGroups(String userid, String project) throws Exception {
		Connection connection = null;
		Group group = null;
		ArrayList<Group> arrGroups = new ArrayList<Group>();
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select user_group_sequence,user_group_name from JUNIPER_USER_GROUP_MASTER where project_sequence = \r\n"
				+ "(select project_sequence from JUNIPER_PROJECT_MASTER where project_id='" + project + "') and user_group_sequence not in "
				+ "(select user_group_sequence from JUNIPER_USER_GROUP_MASTER where USER_GROUP_SEQUENCE in " 
				+ "(select USER_GROUP_SEQUENCE from JUNIPER_UGROUP_USER_LINK where user_sequence=" + "(select user_sequence from JUNIPER_USER_MASTER where user_id='" + userid + "')))");
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			group = new Group();
			group.setGroup_name(rs.getString(2));
			group.setGroup_sequence(rs.getInt(1));
			arrGroups.add(group);
		}
		pstm.close();
		connection.close();
		return arrGroups;
	}

	@Override
	public ArrayList<Group> getGroupsAlready(String userid, String project) throws Exception {
		Connection connection = null;
		Group group = null;
		ArrayList<Group> arrGroups = new ArrayList<Group>();
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select user_group_sequence,user_group_name from JUNIPER_USER_GROUP_MASTER "
				+ " where project_sequence in (select project_sequence from JUNIPER_PROJECT_MASTER where project_id='"+project+"') "
				+ " and  USER_GROUP_SEQUENCE in (select USER_GROUP_SEQUENCE from JUNIPER_UGROUP_USER_LINK where user_sequence IN(select user_sequence from JUNIPER_USER_MASTER where user_id='"+userid+"'))");
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			group = new Group();
			group.setGroup_name(rs.getString(2));
			group.setGroup_sequence(rs.getInt(1));
			arrGroups.add(group);
		}
		pstm.close();
		connection.close();
		return arrGroups;
	}

	@Override
	public ArrayList<String> getAllUsers() throws Exception {
		Connection connection = null;
		ArrayList<String> userid = new ArrayList<String>();
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select user_id from juniper_user_master");
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			userid.add(rs.getString(1));
		}
		pstm.close();
		connection.close();
		return userid;
	}

	@Override
	public String getUser(String user) throws Exception {
		Connection connection = null;
		int stat = 1;
		String userid = null;
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select user_id from juniper_user_master where user_id='" + user + "'");
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			userid = rs.getString(1);
			stat = 0;
			break;
		}
		pstm.close();
		connection.close();
		return stat + userid;
	}

	/*
	 * This method accepts inputs from project registration form and add in project
	 * master table. (non-Javadoc)
	 * 
	 * @see com.iig.gcp.project.dao.ProjectDAO#registerProject(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String registerProject(@Valid String projectId, String projectName, String projectOwner, String projectDescription,String gsdGroup,String gsdKey, int userSequence) throws ClassNotFoundException, Exception {
		
		Connection conn = ConnectionUtils.getConnection();
		
		try {
			String registerProjectQuery = "INSERT INTO" + SPACE + PROJECT_MASTER_TABLE + SPACE + "(project_id,project_name,project_owner,project_description,gsd_group,gsd_key,created_by,created_date )VALUES (" + QUOTE
					+ projectId + QUOTE + COMMA + QUOTE + projectName + QUOTE + COMMA + QUOTE + projectOwner + QUOTE + COMMA + QUOTE + projectDescription + QUOTE + COMMA 
					+ QUOTE + gsdGroup + QUOTE + COMMA
					+ QUOTE + gsdKey + QUOTE + COMMA
					+ QUOTE + userSequence + QUOTE + COMMA
					+ "SYSTIMESTAMP)";
			
			Statement statement = conn.createStatement();
			statement.execute(registerProjectQuery);
			statement.close();
			return "Success";

		} catch (Exception e) {
			System.out.println("Exception :"+e);
			return "Failure";
		}
		finally {
			conn.close();
		}
	}

	@Override
	public ArrayList<Feature> getFeatures(String userid, String project) throws Exception {
		Connection connection = null;
		Feature feature = null;
		ArrayList<Feature> arrFeatures = new ArrayList<Feature>();
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select  f.feature_sequence, f.feature_name,u.user_sequence "
				+ "from juniper_pro_u_feat_link l inner join juniper_user_master u on l.user_sequence=u.user_sequence inner join "
				+ "juniper_project_master p on l.project_sequence=p.project_sequence inner join "
				+ "juniper_feature_master f on l.feature_sequence=f.feature_sequence where u.user_id = ? and p.project_id = ? "
				+ "and (f.FEATURE_PRO_ADMIN = 'N' and f.FEATURE_ADMIN = 'N')");
		pstm.setString(1, userid);
		pstm.setString(2, project);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			feature = new Feature();
			feature.setFeature_sequence(rs.getInt(1));
			feature.setFeature_name(rs.getString(2));
			feature.setSelected_user_sequence(rs.getInt(3));
			arrFeatures.add(feature);
		}
		pstm.close();
		connection.close();
		return arrFeatures;
	}

	@Override
	public ArrayList<Feature> getFeaturesAlready(String userid, String project) throws Exception {
		Connection connection = null;
		Feature feature = null;
		ArrayList<Feature> arrFeatures = new ArrayList<Feature>();
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement(
				"select f.feature_sequence, f.feature_name from juniper_feature_master f left join (select l.feature_sequence from juniper_pro_u_feat_link l inner join juniper_user_master u on l.user_sequence=u.user_sequence inner join juniper_project_master p on l.project_sequence=p.project_sequence where u.user_id=? and p.project_id=?) feat on feat.feature_sequence = f.feature_sequence  where feat.feature_sequence is null and (f.FEATURE_PRO_ADMIN = 'N' AND f.FEATURE_ADMIN = 'N')");

		pstm.setString(1, userid);
		pstm.setString(2, project);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			feature = new Feature();
			feature.setFeature_sequence(rs.getInt(1));
			feature.setFeature_name(rs.getString(2));
			arrFeatures.add(feature);
		}
		pstm.close();
		connection.close();
		return arrFeatures;
	}

	@Override
	public int getUserSequence(String userid) throws Exception {
		int seq = 0;
		Connection connection = null;
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("select user_sequence from  juniper_user_master where user_id=?");

		pstm.setString(1, userid);
		ResultSet rs = pstm.executeQuery();
		while (rs.next()) {
			seq = rs.getInt(1);
		}
		pstm.close();
		connection.close();
		return seq;
	}

	@Override
	public void deleteEntries(int projectseq, int selectUser_Seq) throws Exception {
		Connection connection = null;
		connection = ConnectionUtils.getConnection();
		PreparedStatement pstm = connection.prepareStatement("delete from juniper_pro_u_feat_link where user_sequence=? and project_sequence=?");
		pstm.setInt(1, selectUser_Seq);
		pstm.setInt(2, projectseq);
		pstm.executeUpdate();
		pstm.close();
		connection.close();
	}

	public Project getProject(@Valid String projectId) throws Exception {
		Connection conn = ConnectionUtils.getConnection();
		Project project = new Project();
		String query = "select project_sequence,project_id, project_name,project_owner,project_description,gsd_group,gsd_key from juniper_project_master where project_id = ?";
		PreparedStatement pstm = conn.prepareStatement(query);
		pstm.setString(1, projectId);
		ResultSet rs = pstm.executeQuery();

		while (rs.next()) {

			project.setProject_sequence(rs.getInt(1));
			project.setProject_id(rs.getString(2));
			project.setProject_name(rs.getString(3));
			project.setProject_owner(rs.getString(4));
			project.setProject_description(rs.getString(5));
			project.setGsd_group(rs.getString(6));
			project.setGsd_key(rs.getString(7));
			
		}
		pstm.close();
		conn.close();
		return project;
	}

	@Override
	public String registerAddAdminAccess(int projectSeq, int user_sequence) throws Exception {
		Connection conn = ConnectionUtils.getConnection();
		String featureQuery = "select feature_sequence from juniper_feature_master order by feature_sequence";
		String adminQuery = "select user_sequence from juniper_user_master where is_admin='Y'";
		PreparedStatement featurePstm = conn.prepareStatement(featureQuery);
		PreparedStatement adminPstm = conn.prepareStatement(adminQuery);
		ResultSet adminRs = adminPstm.executeQuery();

		while (adminRs.next()) {
			int adminId = adminRs.getInt(1);
			ResultSet featureRs = featurePstm.executeQuery();
			while (featureRs.next()) {
				int featureId = featureRs.getInt(1);
				String addProject = "INSERT INTO" + SPACE + PROJECT_LINK_TABLE + SPACE + "(user_sequence,project_sequence,feature_sequence)" + "VALUES (" + QUOTE + adminId + QUOTE + COMMA + QUOTE
						+ projectSeq + QUOTE + COMMA + QUOTE + featureId + QUOTE + ")";
				Statement statement = conn.createStatement();
				statement.execute(addProject);
			}
		}
		/*featurePstm.close();
		adminPstm.close();*/
		conn.close();
		return "Success";
	}

	@Override
	public List<String> getProjects() throws Exception {
		List<String> projects = new ArrayList<String>();
		Connection connection = null;
		PreparedStatement pstm =null;
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection.prepareStatement("select project_id  from juniper_project_master");
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				projects.add(rs.getString(1));
			}
		} catch (ClassNotFoundException | SQLException   e) {
			e.printStackTrace();
		} finally {
			pstm.close();
			connection.close();
		}

		return projects;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.iig.gcp.admin.dao.AdminDAO#fetchProjectDetails(java.lang.String)
	 */
	@Override
	public Project fetchProjectDetails(@Valid String projectId) throws Exception {
		Project project = new Project();
		Connection connection = null;
		PreparedStatement pstm = null;
		try {
			connection = ConnectionUtils.getConnection();
			String query = "select project_id, project_name,project_owner,project_description,gsd_group,gsd_key from juniper_project_master where project_id = ?";
			pstm = connection.prepareStatement(query);
			pstm.setString(1, projectId);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				project.setProject_id(rs.getString(1));
				project.setProject_name(rs.getString(2));
				project.setProject_owner(rs.getString(3));
				project.setProject_description(rs.getString(4));
				project.setGsd_group(rs.getString(5));
				project.setGsd_key(rs.getString(6));
			}
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} finally {
			pstm.close();
			connection.close();
		}

		return project;
	}

	/**
	 * 
	 */
	@Override
	public String updateProject(@Valid String projectId, String projectName, String projectOwner, String projectDescription,String gsdGroup,String gsdKey) throws Exception {
		Connection connection = null;
		PreparedStatement pstm =null;
		try {
			connection = ConnectionUtils.getConnection();
			String updateProjectQuery = "update juniper_project_master " + "set project_name =" + QUOTE + projectName + QUOTE + COMMA + "project_owner=" + QUOTE + projectOwner + QUOTE + COMMA
					+ "project_description=" + QUOTE + projectDescription + QUOTE + COMMA
					+ "gsd_group=" + QUOTE + gsdGroup + QUOTE + COMMA
					+ "gsd_key=" + QUOTE + gsdKey + QUOTE 
					+ " where project_id = ?";
			pstm = connection.prepareStatement(updateProjectQuery);
			pstm.setString(1, projectId);
			pstm.execute();
			return "Success";
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return "Failure";
		} finally {
			pstm.close();
			connection.close();
		}
	}

	/**
	 * Commenting this code as of now, may be needed in future. 
	 */
	/*@Override
	public String deleteProject(@Valid String projectId) throws Exception {
		Connection connection;
		try {
			connection = ConnectionUtils.getConnection();
			//int projectSequence = getProjectSeq(projectId);
			String deleteUserProjectLinkQuery = "delete from JUNIPER_PRO_U_FEAT_LINK  where project_sequence in (?)";
			PreparedStatement pstm = connection.prepareStatement(deleteUserProjectLinkQuery);
			pstm.setInt(1, projectSequence);
			pstm.executeUpdate();

			String deleteProjectQuery = "delete from juniper_project_master  where project_id = ?";
			pstm = connection.prepareStatement(deleteProjectQuery);
			pstm.setString(1, projectId);
			pstm.executeUpdate();
			ConnectionUtils.closeQuietly(connection);
			return "Project Deleted";
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return "Project Deletion Failed";
		}
	}*/

	@Override
	public String getProAdminFeatures() throws Exception {
		ArrayList<String> arrfeatureAdmin = new ArrayList<String>();
		Connection conn = ConnectionUtils.getConnection();
		String featureQuery = "select feature_sequence from juniper_feature_master where FEATURE_PRO_ADMIN = 'Y' order by feature_sequence";
		PreparedStatement featurePstm = conn.prepareStatement(featureQuery);

		ResultSet featureRs = featurePstm.executeQuery();
		while (featureRs.next()) {
			arrfeatureAdmin.add(featureRs.getString(1));
		}
		featurePstm.close();
		conn.close();
		return arrfeatureAdmin.toString().replace("[", "").replace("]", "");
		
	};

	/**
	 * 
	 */
	@Override
	public String registerUser(@Valid String userId, String domain, String onboardedUserFullName, String userEmail, String loggedInUser) throws ClassNotFoundException, SQLException, Exception {
		Connection conn = ConnectionUtils.getConnection();
		try {
			String registerUserQuery = "INSERT INTO" + SPACE + USER_MASTER_TABLE + SPACE + "(user_id,user_domain,user_fullname,user_email )"
					+ "VALUES (" + QUOTE + userId + QUOTE+ COMMA 
					+ QUOTE + domain + QUOTE + COMMA 
					+ QUOTE + onboardedUserFullName + QUOTE + COMMA 
					+ QUOTE + userEmail + QUOTE+ ")";
			Statement statement = conn.createStatement();
			statement.execute(registerUserQuery);
			return "Success";

		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}finally {
			conn.close();
		}
	}

	/**
	 * 
	 */
	@Override
	public String updateUser(@Valid String userId, String domain, String onboardedUserFullName, String userEmail, String loggedInUser) throws ClassNotFoundException, SQLException ,Exception {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement pstm =null;
		try {
			String updateUserQuery = "update " + USER_MASTER_TABLE + " set user_domain =" + QUOTE + domain + QUOTE + COMMA + "user_email=" + QUOTE + userEmail + QUOTE + COMMA + "user_fullname=" + QUOTE
					+ onboardedUserFullName + QUOTE
					+ " where user_id = ?";
			 pstm = connection.prepareStatement(updateUserQuery);
			pstm.setString(1, userId);
			pstm.execute();
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		} finally {
			pstm.close();
			connection.close();
		}
	}

	@Override
	public List<String> fetchUserIds() throws ClassNotFoundException, SQLException , Exception {
		ArrayList<String> userIds = new ArrayList<String>();
		Connection conn = ConnectionUtils.getConnection();
		String featureQuery = "select user_id from "+USER_MASTER_TABLE+"";
		PreparedStatement prepStmt = conn.prepareStatement(featureQuery);
		ResultSet userIdsRs = prepStmt.executeQuery();
		while (userIdsRs.next()) {
			userIds.add(userIdsRs.getString(1));
		}
		prepStmt.close();
		conn.close();
		return userIds;
	}

	@Override
	public UserAccount fetchUserAttributes(String userId) throws ClassNotFoundException, SQLException,Exception {
		UserAccount userAccount = new UserAccount();
		Connection conn = ConnectionUtils.getConnection();
		String featureQuery = "select user_id,user_domain,user_fullname,user_email from "+USER_MASTER_TABLE+" where user_id=?";
		PreparedStatement prepStmt = conn.prepareStatement(featureQuery);
		prepStmt.setString(1, userId);
		ResultSet userIdsRs = prepStmt.executeQuery();
		while (userIdsRs.next()) {
			userAccount.setUser_id(userIdsRs.getString(1));
			userAccount.setUser_domain(userIdsRs.getString(2));
			userAccount.setUser_fullname(userIdsRs.getString(3));
			userAccount.setUser_email(userIdsRs.getString(4));
		}
		prepStmt.close();
		conn.close();
		return userAccount;
	}
	/**
	 * 
	 */
	@Override
	public String linkUserGroupsToProject(int userSequence, Project project) throws ClassNotFoundException, SQLException,Exception {
		Connection connection = ConnectionUtils.getConnection();
		PreparedStatement prepStmt =null;
		try {
			Statement statement = connection.createStatement();

			//Below query copy features from GROUP_MASTER template and insert records USER_GROUP_MASTER. 
			String linkUserGroupsToNewProjectQuery = "insert into  " + USER_GROUP_MASTER_TABLE 
					+"(user_group_name,feature_list,project_sequence,created_by)("
					+"select CONCAT('"+project.getProject_id()+"',group_name),feature_list"+COMMA+ project.getProject_sequence()+COMMA+"(select user_id from "+USER_MASTER_TABLE+" where USER_SEQUENCE='"+userSequence+"') from "+GROUP_MASTER_TABLE+" where group_sequence not in ('1'))";
			statement.execute(linkUserGroupsToNewProjectQuery);

			//Below query add user to UGROUP_USER mapping table.
			String addUserGroupToUserMappingQuery = "insert into  " + UGROUP_GROUP_TABLE 
					+"(user_group_sequence,user_sequence,project_sequence)("
					+"select user_group_sequence"+COMMA
					+QUOTE+ userSequence+QUOTE+COMMA
					+QUOTE+ project.getProject_sequence()+QUOTE
					+" from "+USER_GROUP_MASTER_TABLE+" where user_group_name not in('"+JUNIPER_JADMIN_GROUP+"') and project_sequence='"+project.getProject_sequence()+"')";
			statement.execute(addUserGroupToUserMappingQuery);

			//Below query will add access to all existing JAdmins to existing project.
			List<UserAccount> jadminIds = new ArrayList<UserAccount>();
			String fetchAdminUsers = "select distinct user_sequence from "+UGROUP_GROUP_TABLE+" where user_group_sequence in (select user_group_sequence from "+USER_GROUP_MASTER_TABLE+" where user_group_name in('"+JUNIPER_JADMIN_GROUP+"'))";
			prepStmt = connection.prepareStatement(fetchAdminUsers);
			ResultSet jadminUserIds = prepStmt.executeQuery();
			while (jadminUserIds.next()) {
				UserAccount user = new UserAccount();
				user.setUser_sequence(Integer.parseInt(jadminUserIds.getString(1)));
				jadminIds.add(user);
			}

			for(UserAccount user:jadminIds) {
				String addAccessToAllJadmins = "insert into  " + UGROUP_GROUP_TABLE 
						+"(user_group_sequence,user_sequence,project_sequence)("
						+"select user_group_sequence"+COMMA
						+QUOTE+ user.getUser_sequence()+QUOTE+COMMA
						+QUOTE+ project.getProject_sequence()+QUOTE
						+" from "+USER_GROUP_MASTER_TABLE+" where user_group_name in('"+JUNIPER_JADMIN_GROUP+"'))";
				statement.execute(addAccessToAllJadmins);
			}
			return "Success";
		} catch (Exception e) {
			System.out.println("Exception "+e);
			return "Failure";
		}finally {
			prepStmt.close();
			connection.close();
		}
	}


	/**
	 * This method check if PSID already exists in database or not.
	 * 
	 * @param: String psid
	 * @return 1 if EIM already exists otherwise returns 0.
	 */
	@Override
	public int validatePSID(@Valid String psid) throws Exception {
		Connection connection = null;
		int stat = 0;
		PreparedStatement pstm = null;
		try {
			connection = ConnectionUtils.getConnection();
			pstm = connection
					.prepareStatement("select user_id from " + USER_MASTER_TABLE + " where user_id=?");
			pstm.setString(1, psid);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				stat = 1;
				break;
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Exception "+e);
		}finally {
			connection.close();
		}
		pstm.close();
		return stat;
	}
	
	
	@Override
	public ArrayList<Project> getProjects1(String username) throws Exception {

		ArrayList<Project> arrProject=new ArrayList<Project>();
		String sql =  "SELECT  DISTINCT p.PROJECT_ID ,p.project_sequence FROM "+UGROUP_GROUP_TABLE+" l inner join "
				+ "juniper_project_master p on l.project_sequence=p.project_sequence where user_sequence in "
				+ "(select user_sequence from "+USER_MASTER_TABLE+" where user_id=?)order by p.project_sequence desc";
		Connection  conn= ConnectionUtils.getConnection();
		PreparedStatement pstm = conn.prepareStatement(sql);
		pstm.setString(1, username);
		ResultSet rs = pstm.executeQuery();
		Project prj =null;
		while (rs.next()) {
			prj=new Project();
			prj.setProject_id(rs.getString(1));
			prj.setProject_sequence(rs.getInt(2));
			arrProject.add(prj);	
		}
		pstm.close();
		conn.close();
		return arrProject;

	}
	
	
	/**
	 * This method fetch list of countries mapped with region from database
	 * 
	 * @param : String - region
	 * @return: List of countries
	 * @throws Exception 
	 */
	@Override
	public List<CountryMaster> fetchCountries(@Valid String region) throws Exception {
		CountryMaster cm = null;
		ArrayList<CountryMaster> countries = new ArrayList<CountryMaster>();
		Connection  conn= null;
		PreparedStatement pstm =null;
		try {
			 conn= ConnectionUtils.getConnection();
			String query ="select country_code,country_name from "+ COUNTRY_REGION_MAPPING_TABLE+"  where region_name=? order by country_name ";
			pstm = conn.prepareStatement(query);
			pstm.setString(1, region);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				cm = new CountryMaster();
				cm.setCountry_code(rs.getString(1));
				cm.setCountry_name(rs.getString(2));
				countries.add(cm);
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Exception "+e);
		}finally {
			pstm.close();
			conn.close();
		}
		return countries;
		}

	/**
	 * This method check if EIM already exists in database or not.
	 * 
	 * @param: String eim
	 * @return 1 if EIM already exists otherwise returns 0.
	 * @throws Exception 
	 */
	@Override
	public int checkEIM(@Valid String system_eim) throws Exception {
		int stat = 0;
		Connection  conn=null;
		PreparedStatement pstm=null;
		try {
			 conn= ConnectionUtils.getConnection();
			 pstm = conn.prepareStatement("select system_eim from " + SYSTEM_MASTER_TABLE + " where system_eim=?");
			pstm.setString(1, system_eim);
			ResultSet rs = pstm.executeQuery();
			while (rs.next()) {
				stat = 1;
				break;
			}
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("Exception "+e);
		}finally {
			pstm.close();
			conn.close();
		}
		
		return stat;
	}

	/**
	 * This method register system form details and push them in database.
	 * @throws Exception 
	 */
	@Override
	public String registerSystem(@Valid String systemEIM, String systemName, String region, String country,
			String environmentType, String userName)
			throws Exception {
		Connection  conn= ConnectionUtils.getConnection();
		String registerProjectQuery;
		try {
				registerProjectQuery = "INSERT INTO" + SPACE + SYSTEM_MASTER_TABLE + SPACE
						+ "(system_eim,system_name,system_region,system_country,environment_type,created_by,created_date)"
						+ "VALUES (" + QUOTE + systemEIM + QUOTE + COMMA + QUOTE + systemName + QUOTE + COMMA + QUOTE
						+ region + QUOTE + COMMA + QUOTE + country + QUOTE + COMMA 
						+ QUOTE + environmentType + QUOTE+ COMMA + QUOTE + userName + QUOTE + COMMA + "SYSTIMESTAMP"
						+ ")";
			Statement statement = conn.createStatement();
			statement.execute(registerProjectQuery);
			return "Success";
		} catch (Exception e) {
			System.out.println("Exception "+e);
			return "Failure";
		} finally {
			conn.close();
		}
	}
}