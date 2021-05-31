CREATE TABLE users (  u_id int(11) NOT NULL default '0',  u_email varchar(250) NOT NULL default '',  u_password varchar(250) NOT NULL default '',  u_lastname varchar(80) NOT NULL default '',  u_firstname varchar(80) NOT NULL default '',  u_nickname varchar(80) NOT NULL default '',  u_active tinyint(1) NOT NULL default '0',  PRIMARY KEY  (u_id),  UNIQUE KEY u_email (u_email) );

CREATE TABLE roles (
  r_id int(11) NOT NULL default '0',  r_mask int(11) NOT NULL default '0',  r_title varchar(100) default NULL, PRIMARY KEY (r_id)
  );

CREATE TABLE nodes (  n_id int(11) NOT NULL default '0',  n_type int(11) NOT NULL default '0',  n_title varchar(100) default NULL,  n_owner int(11) NOT NULL default '0',  n_creation_date timestamp NOT NULL,  PRIMARY KEY  (n_id) );

CREATE TABLE node2parent (  np_parent int(11) NOT NULL default '0',  np_child int(11) NOT NULL default '0',  PRIMARY KEY  (np_parent,np_child) );

CREATE TABLE users_nodes_roles (  unr_user int(11) NOT NULL default '0',  unr_node int(11) NOT NULL default '0',  unr_role int(11) NOT NULL default '0',  PRIMARY KEY  (unr_user,unr_node,unr_role) ) ;

INSERT INTO `nodes` (`n_id`,`n_type`,`n_title`,`n_owner`,`n_creation_date`) VALUES (1,0,'hello',0,'2016-09-18 20:23:30');
