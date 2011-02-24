--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('HELPDESK_MANAGEMENT','helpdesk.adminFeature.helpdesk_management.name',1,'jsp/admin/plugins/helpdesk/ManageHelpdesk.jsp','helpdesk.adminFeature.helpdesk_management.description',0,'helpdesk','APPLICATIONS','images/admin/skin/plugins/helpdesk/helpdesk.png',NULL);


--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right (id_right,id_user) VALUES ('HELPDESK_MANAGEMENT',1);
INSERT INTO core_user_right (id_right,id_user) VALUES ('HELPDESK_MANAGEMENT',2);


--
-- Dumping data for table core_admin_role
--
INSERT INTO core_admin_role (role_key,role_description) VALUES ('helpdesk_manager','Gestion du module de F.A.Q.');


--
-- Dumping data for table core_admin_role_resource
--
INSERT INTO core_admin_role_resource (rbac_id,role_key,resource_type,resource_id,permission) VALUES 
 (10,'helpdesk_manager','HELPDESK_FAQ','*','*');


--
-- Dumping data for table core_user_role
--
INSERT INTO core_user_role (role_key,id_user) VALUES ('helpdesk_manager',1);
INSERT INTO core_user_role (role_key,id_user) VALUES ('helpdesk_manager',2);