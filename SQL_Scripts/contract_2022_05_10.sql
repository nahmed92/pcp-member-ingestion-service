
CREATE TABLE dbo.contract (
	id varchar(200) NOT NULL,
	contract_json varchar(MAX) ,
	mtv_person_ids varchar(2000) ,
	claims_ids varchar(2000) ,
	state_codes varchar(1000) ,
	num_of_attempt int NULL,
	num_of_enrollee int NULL,
	publish_status varchar(10) NOT NULL,
	created_at datetime NULL,
	last_updated_at datetime NULL,
	PRIMARY KEY (id)
);

ALTER TABLE [dbo].[contract] ADD	[v_contract_id]  AS (CONVERT([varchar](20),json_value([contract_json],'$.contractID'))) PERSISTED;

CREATE  INDEX [idx_v_contract_id] ON [dbo].[contract](v_contract_id);

CREATE  INDEX [idx_publish_status] ON [dbo].[contract](publish_status);

CREATE  INDEX [idx_state_codes] ON [dbo].[contract](state_codes);


