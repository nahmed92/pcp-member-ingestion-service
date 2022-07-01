CREATE TABLE dbo.contract_audit (
	id varchar(255) NOT NULL,
	revision_number int NOT NULL,
	claims_ids varchar(2000) NULL,
	contract_id varchar(255) NULL,
	contract_json varchar(MAX) NULL,
	created_at datetime2 NULL,
	last_updated_at datetime2 NULL,
	mtv_person_ids varchar(2000) NULL,
	num_of_retries int NULL,
	num_of_enrollee int NULL,
	publish_status varchar(10) NULL,
	revision_type varchar(10) NULL,
	state_codes varchar(1000) NULL,
	audit_timestamp datetime2 NULL,
	CONSTRAINT PK_contract_audit_id_rev_num PRIMARY KEY (id,revision_number)
);


CREATE  INDEX [idx_contract_audit_contract_id] ON [dbo].[contract_audit](contract_id);

