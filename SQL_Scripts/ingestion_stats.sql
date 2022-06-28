-- [PCP-Members-Ingestion].dbo.ingestion_stats definition

-- Drop table

-- DROP TABLE [PCP-Members-Ingestion].dbo.ingestion_stats;

CREATE TABLE [PCP-Members-Ingestion].dbo.ingestion_stats (
	id varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	run_id varchar(100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	state varchar(3) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	no_of_contracts int NOT NULL,
	start_time datetime NOT NULL,
	end_time datetime NOT NULL,
	created_at datetime NOT NULL,
	last_updated_at datetime NOT NULL,
	service_instance_id varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	CONSTRAINT ingestion_stats_PK PRIMARY KEY (id)
);