/*DROP SCRIPTS*/
DROP TABLE IF EXISTS admin_audit_trail;
DROP TABLE IF EXISTS admin_user;
DROP TABLE IF EXISTS role_access;
DROP TABLE IF EXISTS main_menu;
DROP TABLE IF EXISTS page_access;
DROP TABLE IF EXISTS api_access;
DROP TABLE IF EXISTS action_access;
DROP TABLE IF EXISTS slu_admin_user_status;
DROP TABLE IF EXISTS slu_page_access_status;
DROP TABLE IF EXISTS slu_api_access_status;
DROP TABLE IF EXISTS slu_role_access_status;
DROP TABLE IF EXISTS slu_country_data;
DROP TABLE IF EXISTS slu_admin_audit_trail_type;

/*CREATE SCRIPTS*/
--Country Data SLU Creation Start
CREATE TABLE slu_country_data
(
    id SERIAL NOT NULL,
    country_code VARCHAR(10) NOT NULL,
    country_name VARCHAR(200) NOT NULL,
    country_calling_code VARCHAR(10) NOT NULL,
    country_flag_data TEXT NOT NULL,
    special_sort INT,
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id),
	UNIQUE (country_code)
);
--Country Data SLU Creation End
--Country Data SLU Initial Data Start
ALTER SEQUENCE slu_country_data_id_seq RESTART WITH 1;
--Country Data SLU Initial Data End

--Role Access Status SLU Creation Start
CREATE TABLE slu_role_access_status
(
    id SERIAL NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
	UNIQUE (status_name)
);
--Role Access Status SLU Creation End
--Role Access Status SLU Initial Data Start
INSERT INTO slu_role_access_status (id,status_name) VALUES (1,'ACTIVE');
INSERT INTO slu_role_access_status (id,status_name) VALUES (2,'DISABLED');
ALTER SEQUENCE slu_role_access_status_id_seq RESTART WITH 3;
--Role Access Status SLU Initial Data End

--Role Access Creation Start
CREATE TABLE role_access
(
    id SERIAL NOT NULL,
    role_name VARCHAR(100) NOT NULL,
    menu_access_dstr VARCHAR(100) NOT NULL,
    page_access_dstr VARCHAR(100) NOT NULL,
    api_access_dstr VARCHAR(100) NOT NULL,
    action_access_dstr VARCHAR(100) NOT NULL,
    role_status INTEGER NOT NULL REFERENCES slu_role_access_status(id),
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
	UNIQUE (role_name)
);
--Role Access Creation End
--Role Access Initial Data Start
INSERT INTO role_access (id,role_name,menu_access_dstr,page_access_dstr,api_access_dstr,action_access_dstr,role_status) VALUES (1,'System Admin','FFFFFF','FFFFFF','FFFFFFFFFFFFFFFFFFF','FFFFFFFFFFFFFFFFFFF',1);
ALTER SEQUENCE role_access_id_seq RESTART WITH 8;
--Role Access Initial Data End

--Action Access Creation Start
CREATE TABLE action_access
(
    id SERIAL NOT NULL,
    action_name VARCHAR(100) NOT NULL,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    is_hidden BOOLEAN NOT NULL DEFAULT false,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
	UNIQUE (action_name)
);
--Action Access Creation End
--Action Access Initial Data Start
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (1,'Admin Page Management Create',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (2,'Admin Page Management Update',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (3,'Admin API Management Create',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (4,'Admin API Management Update',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (5,'Admin Menu Management Create Main Menu',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (6,'Admin Menu Management Create Sub Menu',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (7,'Admin Menu Management Edit Main Menu',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (8,'Admin Menu Management Edit Sub Menu',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (9,'Admin Menu Management Update All Menu',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (10,'Admin Role Management Create',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (11,'Admin Role Management Update',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (12,'Admin User Management Create',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (13,'Admin User Management Update',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (14,'Admin User Management Reset Password',true,false);
INSERT INTO action_access (id,action_name,is_enabled,is_hidden) VALUES (15,'Admin User Management Export',true,false);
ALTER SEQUENCE action_access_id_seq RESTART WITH 16;
--Action Access Initial Data End

--Admin User Status SLU Creation Start
CREATE TABLE slu_admin_user_status
(
    id SERIAL NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
	UNIQUE (status_name)
);
--Admin User Status SLU Creation End
--Admin User Status SLU Initial Data Start
INSERT INTO slu_admin_user_status (id,status_name) VALUES (1,'PENDING');
INSERT INTO slu_admin_user_status (id,status_name) VALUES (2,'ACTIVE');
INSERT INTO slu_admin_user_status (id,status_name) VALUES (3,'SUSPENDED');
ALTER SEQUENCE slu_admin_user_status_id_seq RESTART WITH 4;
--Admin User Status SLU Initial Data End

--Admin User Creation Start
CREATE TABLE admin_user
(
    id BIGSERIAL NOT NULL,    
    login_id VARCHAR(100) NOT NULL,
    login_password VARCHAR(200) NOT NULL,
    role_access INTEGER NOT NULL REFERENCES role_access(id),
    full_name VARCHAR(200) NOT NULL,
    display_name VARCHAR(50) NOT NULL,
    email VARCHAR(200) NOT NULL,
    user_status INTEGER NOT NULL REFERENCES slu_admin_user_status(id),
    failed_login_attempt INTEGER NOT NULL DEFAULT 0,
    should_change_password BOOLEAN NOT NULL DEFAULT false,
    last_login_time TIMESTAMPTZ,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE (login_id),
    UNIQUE (email)
);
--Admin User Creation End
--Admin User Initial Data Start
INSERT INTO admin_user (id,login_id,login_password,role_access,full_name,display_name,email,user_status) VALUES (1,'saadmin','$2a$10$c9rTAAWN82o1l/YLzn9rJO22xBBf88cRcUH1eSwTNklVZtadMbURO',1,'SA Admin','SA Admin','saadmin@admin.com',2);
ALTER SEQUENCE admin_user_id_seq RESTART WITH 2;
--Admin User Initial Data End

--Main Menu Creation Start
CREATE TABLE main_menu
(
    id SERIAL NOT NULL,
    menu_name VARCHAR(100) NOT NULL,
    locale_name VARCHAR(200) NOT NULL,
    nav_name VARCHAR(100) NOT NULL,
    icon_html VARCHAR(1000),
    redirect_link VARCHAR (500),
    menu_sequence INTEGER NOT NULL,
    parent_id INTEGER,
    is_enabled BOOLEAN NOT NULL DEFAULT true,
    is_hidden BOOLEAN NOT NULL DEFAULT false,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
--Main Menu Creation End
--Main Menu Initial Data Start
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (1,'Dashboard','admin.nav.dashboard','dashboard','<i class="fas fa-fw fa-tachometer-alt"></i>','#!dashboard',1,NULL,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (2,'Audit Trail','admin.nav.audit_trail','audit_trail','<i class="fas fa-history"></i>','#!audit_trail',2,NULL,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (3,'Access Management','admin.nav.access_management','access_management','<i class="fas fa-fw fa-unlock-alt"></i>',NULL,3,NULL,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (4,'Page Management','admin.nav.access_management.page_management','page_management','<i class="fas fa-fw fa-file"></i>','#!page_management',1,3,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (5,'API Management','admin.nav.access_management.api_management','api_management','<i class="fas fa-fw fa-network-wired"></i>','#!api_management',2,3,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (6,'Menu Management','admin.nav.access_management.menu_management','menu_management','<i class="fas fa-fw fa-clipboard-list"></i>','#!menu_management',3,3,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (7,'Role Management','admin.nav.access_management.role_management','role_management','<i class="fas fa-fw fa-user-tag"></i>','#!role_management',4,3,false);
INSERT INTO main_menu (id,menu_name,locale_name,nav_name,icon_html,redirect_link,menu_sequence,parent_id,is_hidden) VALUES (8,'User Management','admin.nav.user_management','user_management','<i class="fas fa-fw fa-users"></i>','#!user_management',4,NULL,false);
ALTER SEQUENCE main_menu_id_seq RESTART WITH 9;
--Main Menu Initial Data End

--Page Access Status SLU Creation Start
CREATE TABLE slu_page_access_status
(
    id SERIAL NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
	UNIQUE (status_name)
);
--Page Access Status SLU Creation End
--Page Access Status SLU Initial Data Start
INSERT INTO slu_page_access_status (id,status_name) VALUES (1,'UNDER_DEVELOPMENT');
INSERT INTO slu_page_access_status (id,status_name) VALUES (2,'ACTIVE');
INSERT INTO slu_page_access_status (id,status_name) VALUES (3,'UNDER_MAINTENANCE');
ALTER SEQUENCE slu_page_access_status_id_seq RESTART WITH 4;
--Page Access Status SLU Initial Data End

--Page Access Creation Start
CREATE TABLE page_access
(
    id SERIAL NOT NULL,
    page_name VARCHAR(50) NOT NULL,
    page_desc VARCHAR(100) NOT NULL,
	page_status INTEGER NOT NULL REFERENCES slu_page_access_status(id),
	is_hidden BOOLEAN NOT NULL DEFAULT false,
	created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
	UNIQUE (page_name),
	UNIQUE (page_desc)
);
--Page Access Creation End
--Page Access Initial Data Start
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (1,'dashboard','Admin Dashboard',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (2,'audit_trail','Admin Audit Trail',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (3,'page_management','Admin Page Management',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (4,'api_management','Admin API Management',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (5,'role_management','Admin Role Management',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (6,'menu_management','Admin Menu Management',2,false);
INSERT INTO page_access (id,page_name,page_desc,page_status,is_hidden) VALUES (7,'user_management','Admin User Management',2,false);
ALTER SEQUENCE page_access_id_seq RESTART WITH 8;
--Page Access Initial Data End

--API Access Status SLU Creation Start
CREATE TABLE slu_api_access_status
(
    id SERIAL NOT NULL,
    status_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
	UNIQUE (status_name)
);
--API Access Status SLU Creation End
--API Access Status SLU Initial Data Start
INSERT INTO slu_api_access_status (id,status_name) VALUES (1,'ACTIVE');
INSERT INTO slu_api_access_status (id,status_name) VALUES (2,'DISABLED');
ALTER SEQUENCE slu_api_access_status_id_seq RESTART WITH 3;
--API Access Status SLU Initial Data End

--API Access Creation Start
CREATE TABLE api_access
(
    id SERIAL NOT NULL,
    api_name VARCHAR(100) NOT NULL,
    api_desc VARCHAR(100) NOT NULL,
    api_status INTEGER NOT NULL REFERENCES slu_api_access_status(id),
    is_hidden BOOLEAN NOT NULL DEFAULT false,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
	UNIQUE (api_name),
	UNIQUE (api_desc)
);
--API Access Creation End
--API Access Initial Data Start
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (1,'/admin_api/dashboard/get_dashboard_data','Get Admin Dashboard Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (2,'/admin_api/menu_management/get_menu_list','Get Admin Menu Listing API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (3,'/admin_api/menu_management/update_menu','Update Admin Menu API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (4,'/admin_api/page_access/get_page_init_data','Get Admin Page Init Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (5,'/admin_api/page_access/get_page_list','Get Admin Page Listing API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (6,'/admin_api/page_access/create_page','Create Admin Page API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (7,'/admin_api/page_access/update_page','Update Admin Page API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (8,'/admin_api/api_access/get_api_init_data','Get Admin API Init Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (9,'/admin_api/api_access/get_api_list','Get Admin API Listing API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (10,'/admin_api/api_access/create_api','Create Admin API API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (11,'/admin_api/api_access/update_api','Update Admin API API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (12,'/admin_api/role_access/get_role_init_data','Get Admin Role Init Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (13,'/admin_api/role_access/get_role_list','Get Admin Role Listing API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (14,'/admin_api/role_access/create_role','Create Admin Role API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (15,'/admin_api/role_access/update_role','Update Admin Role API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (16,'/admin_api/user_management/get_user_init_data','Get Admin User Init Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (17,'/admin_api/user_management/get_user_list','Get Admin User Listing API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (18,'/admin_api/user_management/create_user','Create Admin User API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (19,'/admin_api/user_management/update_user','Update Admin User API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (20,'/admin_api/user_management/reset_password','Reset Admin User Password API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (21,'/admin_api/my_profile/change_password','Change Admin User Password API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (22,'/admin_api/audit_trail/get_audit_trail_init_data','Get Admin Audit Trail Init Data API',1,false);
INSERT INTO api_access (id,api_name,api_desc,api_status,is_hidden) VALUES (23,'/admin_api/audit_trail/get_audit_trail_list','Get Admin Audit Trail List API',1,false);
ALTER SEQUENCE api_access_id_seq RESTART WITH 24;
--API Access Initial Data End

--Admin Audit Trail Type SLU Creation Start
CREATE TABLE slu_admin_audit_trail_type
(
    id SERIAL NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
	UNIQUE (type_name)
);
--Admin Audit Trail Type SLU Creation End
--Admin Audit Trail Type SLU Initial Data Start
INSERT INTO slu_admin_audit_trail_type (id,type_name) VALUES (1,'ROLE ACCESS');
INSERT INTO slu_admin_audit_trail_type (id,type_name) VALUES (2,'USER');
ALTER SEQUENCE slu_admin_audit_trail_type_id_seq RESTART WITH 3;
--Admin Audit Trail Type SLU Initial Data End

--Admin Audit Trail Creation Start
CREATE TABLE admin_audit_trail
(
    id BIGSERIAL NOT NULL,
    user_id BIGINT NOT NULL REFERENCES admin_user(id),
    audit_type BIGINT NOT NULL REFERENCES slu_admin_audit_trail_type(id),
    action_desc TEXT NOT NULL,
    created_time TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
--Admin Audit Trail Creation End
--Admin Audit Trail Initial Data Start
--Admin Audit Trail Initial Data End