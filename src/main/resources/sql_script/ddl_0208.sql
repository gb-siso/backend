drop table if exists dislike;
drop table if exists `like`;
drop table if exists rating;
drop table if exists congressman;
drop table if exists member;

create table congressman (times_elected integer not null, created_date datetime(6), id bigint not null auto_increment, modified_date datetime(6), name varchar(255) not null, party varchar(255) not null, primary key (id)) engine=InnoDB;

create table dislike (congressman_id bigint, id bigint not null auto_increment, member_id bigint, rating_id bigint, dtype varchar(31) not null, primary key (id)) engine=InnoDB;

create table `like` (congressman_id bigint, created_date datetime(6), id bigint not null auto_increment, member_id bigint, modified_date datetime(6), rating_id bigint, dtype varchar(31) not null, primary key (id), check ((dtype = 'RatingLike' AND rating_id IS NOT NULL AND congressman_id IS NULL) OR (dtype = 'CongressmanLike' AND congressman_id IS NOT NULL AND rating_id IS NULL))) engine=InnoDB;

create table member (created_date datetime(6), id bigint not null auto_increment, kakao_id bigint, modified_date datetime(6), image_url varchar(255), naver_id varchar(255), nickname varchar(255) not null, refresh_token varchar(255), primary key (id)) engine=InnoDB;

create table rating (rate float(53) not null, congressman_id bigint not null, created_date datetime(6), id bigint not null auto_increment, member_id bigint not null, modified_date datetime(6), content varchar(255) not null, primary key (id)) engine=InnoDB;

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