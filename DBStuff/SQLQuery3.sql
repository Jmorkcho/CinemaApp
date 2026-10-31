select * from dbo.users
select * from dbo.confirmation_token
select * from dbo.movies
select * from dbo.genres

INSERT INTO dbo.users
VALUES ('admin','$2a$10$iEUacPYzXI5FtB69V5Dk0umAEe02cHzpf5aygnK936jzcIP3gcuZe','notnecessary','admin','admin','100','2','1','2050-05-26','1')



delete from dbo.users where id=2018;

SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('dbo.movies') 