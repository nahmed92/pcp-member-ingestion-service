-- [PCP-Members-Ingestion].dbo.ingestion_controller definition

-- Drop table

-- DROP TABLE [PCP-Members-Ingestion].dbo.ingestion_controller;

CREATE TABLE [PCP-Members-Ingestion].dbo.ingestion_controller (
	id varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	run_id varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	run_timestamp datetime NOT NULL,
	status varchar(20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	states varchar(200) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	created_at datetime NOT NULL,
	last_updated_at datetime NOT NULL,
	no_of_contracts int NOT NULL,
	service_instance_id varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	CONSTRAINT ingestion_controller_PK PRIMARY KEY (id)
);