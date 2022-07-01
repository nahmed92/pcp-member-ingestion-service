CREATE TABLE [PCP-Members-Ingestion].dbo.contract_audit (
	id varchar(255) NOT NULL,
	revision_number int NOT NULL,
	claims_ids varchar(255) NULL,
	contract_id varchar(255) NULL,
	contract_json varchar(MAX) NULL,
	created_at datetime2 NULL,
	last_updated_at datetime2 NULL,
	mtv_person_ids varchar(255) NULL,
	num_of_retries int NULL,
	num_of_enrollee int NULL,
	publish_status varchar(255) NULL,
	revision_type varchar(255) NULL,
	state_codes varchar(255) NULL,
	audit_timestamp datetime2 NULL,
	CONSTRAINT PK__contract__BFCB4870A1816C5D PRIMARY KEY (id,revision_number)
);