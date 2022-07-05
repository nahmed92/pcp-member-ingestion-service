
CREATE TABLE .dbo.ingestion_stats (
	id varchar(100) NOT NULL,
	run_id varchar(100) NOT NULL,
	state varchar(3) NOT NULL,
	no_of_contracts int,
	start_time datetime,
	end_time datetime ,
	created_at datetime NOT NULL,
	last_updated_at datetime NOT NULL,
	service_instance_id varchar(50) NOT NULL,
	PRIMARY KEY (id)
);

CREATE  INDEX [idx_ingestion_stats_run_id] ON [dbo].[ingestion_stats](run_id);

CREATE  INDEX [idx_ingestion_stats_service_instance_id] ON [dbo].[ingestion_stats](service_instance_id);