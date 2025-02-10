drop table if exists dislike;
drop table if exists `like`;
drop table if exists rating;
drop table if exists congressman;
drop table if exists member;

create table congressman (times_elected integer, created_date datetime(6), id bigint not null auto_increment, modified_date datetime(6), name varchar(255), party varchar(255), primary key (id)) engine=InnoDB;

create table dislike (congressman_id bigint, id bigint not null auto_increment, member_id bigint, rating_id bigint, dtype varchar(31) not null, primary key (id)) engine=InnoDB;

create table `like` (congressman_id bigint, created_date datetime(6), id bigint not null auto_increment, member_id bigint, modified_date datetime(6), rating_id bigint, dtype varchar(31) not null, primary key (id)) engine=InnoDB;

create table member (created_date datetime(6), id bigint not null auto_increment, kakao_id bigint unique, naver_id bigint unique, modified_date datetime(6), image_url varchar(255), nickname varchar(255), refresh_token varchar(255), primary key (id)) engine=InnoDB;

create table rating (rate float(53), congressman_id bigint, created_date datetime(6), id bigint not null auto_increment, member_id bigint, modified_date datetime(6), content varchar(255), primary key (id)) engine=InnoDB;

alter table dislike add constraint unique_rating_member unique (rating_id, member_id);

alter table `like` add constraint unique_like_rating_member unique (rating_id, member_id);

alter table dislike add constraint fk_dislike_member foreign key (member_id) references member (id);

alter table dislike add constraint fk_dislike_congressman foreign key (congressman_id) references congressman (id);

alter table dislike add constraint fk_dislike_rating foreign key (rating_id) references rating (id);

alter table `like` add constraint fk_like_member foreign key (member_id) references member (id);

alter table `like` add constraint fk_like_congressman foreign key (congressman_id) references congressman (id);

alter table `like` add constraint fk_like_rating foreign key (rating_id) references rating (id);

alter table rating add constraint fk_rating_congressman foreign key (congressman_id) references congressman (id);

alter table rating add constraint fk_rating_member foreign key (member_id) references member (id);