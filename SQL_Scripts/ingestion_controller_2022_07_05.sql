
CREATE TABLE dbo.ingestion_controller (
	id varchar(200) NOT NULL,
	run_id varchar(200) NOT NULL,
	run_timestamp datetime,
	status varchar(20) NOT NULL,
	states varchar(200) NOT NULL,
	created_at datetime NOT NULL,
	last_updated_at datetime NOT NULL,
	no_of_contracts int NULL,
	no_of_days int NULL,
	cut_off_date date,
	service_instance_id varchar(50) NULL,
	PRIMARY KEY (id)
);


CREATE  INDEX [idx_ingestion_controller_status] ON [dbo].[ingestion_controller](status);

CREATE  INDEX [idx_ingestion_controller_service_instance_id] ON [dbo].[ingestion_controller](service_instance_id);