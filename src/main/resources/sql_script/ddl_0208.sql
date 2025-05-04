drop table if exists dislike;
drop table if exists `like`;
drop table if exists rating;
drop table if exists congressman;
drop table if exists member;
drop table if exists assembly_session;
drop table if exists congressman_bill;
drop table if exists bill;
drop table if exists bill_summary;

create table congressman
(
    created_date       datetime(6),
    id                 bigint       not null auto_increment,
    modified_date      datetime(6),
    code               varchar(255) not null,
    electoral_district varchar(255),
    electoral_type     varchar(255),
    image_url          varchar(255),
    name               varchar(255) not null,
    party              varchar(255) not null,
    position           varchar(255),
    sex                varchar(255),
    times_elected      varchar(255) not null,
    primary key (id)
) engine = InnoDB;

create table assembly_session
(
    session        integer not null,
    congressman_id bigint  not null,
    created_date   datetime(6),
    id             bigint  not null auto_increment,
    modified_date  datetime(6),
    primary key (id)
) engine = InnoDB;

create table dislike
(
    congressman_id bigint,
    id             bigint      not null auto_increment,
    member_id      bigint,
    rating_id      bigint,
    dtype          varchar(31) not null,
    created_date   datetime(6),
    modified_date  datetime(6),
    primary key (id),
    check ((dtype = 'RatingDisLike' AND rating_id IS NOT NULL AND congressman_id IS NULL) OR
           (dtype = 'CongressmanDisLike' AND congressman_id IS NOT NULL AND rating_id IS NULL))
) engine = InnoDB;

create table `like`
(
    congressman_id bigint,
    created_date   datetime(6),
    id             bigint      not null auto_increment,
    member_id      bigint,
    modified_date  datetime(6),
    rating_id      bigint,
    dtype          varchar(31) not null,
    primary key (id),
    check ((dtype = 'RatingLike' AND rating_id IS NOT NULL AND congressman_id IS NULL) OR
           (dtype = 'CongressmanLike' AND congressman_id IS NOT NULL AND rating_id IS NULL))
) engine = InnoDB;

create table member
(
    created_date  datetime(6),
    id            bigint       not null auto_increment,
    kakao_id      bigint,
    modified_date datetime(6),
    image_url     varchar(255),
    naver_id      varchar(255),
    nickname      varchar(255) not null,
    refresh_token varchar(255),
    role          varchar(20)  not null,
    primary key (id)
) engine = InnoDB;

create table rating
(
    rate           float(53)    not null,
    congressman_id bigint       not null,
    created_date   datetime(6),
    id             bigint       not null auto_increment,
    member_id      bigint       not null,
    modified_date  datetime(6),
    content        varchar(255) not null,
    primary key (id)
) engine = InnoDB;

alter table dislike
    add constraint unique_rating_member unique (rating_id, member_id);

alter table `like`
    add constraint unique_like_rating_member unique (rating_id, member_id);

alter table dislike
    add constraint fk_dislike_member foreign key (member_id) references member (id);

alter table dislike
    add constraint fk_dislike_congressman foreign key (congressman_id) references congressman (id);

alter table dislike
    add constraint fk_dislike_rating foreign key (rating_id) references rating (id);

alter table `like`
    add constraint fk_like_member foreign key (member_id) references member (id);

alter table `like`
    add constraint fk_like_congressman foreign key (congressman_id) references congressman (id);

alter table `like`
    add constraint fk_like_rating foreign key (rating_id) references rating (id);

alter table rating
    add constraint fk_rating_congressman foreign key (congressman_id) references congressman (id);

alter table rating
    add constraint fk_rating_member foreign key (member_id) references member (id);


alter table congressman
    add constraint unique_congressman_code unique (code);

alter table assembly_session
    add constraint fk_assembly_session_congressman
        foreign key (congressman_id)
            references congressman (id);

alter table assembly_session
    drop
        foreign key fk_assembly_session_congressman;

alter table rating
    drop
        foreign key fk_rating_congressman;



create table bill
(
    cmt_present_dt     date,
    cmt_proc_dt        date,
    committee_dt       date,
    law_present_dt     date,
    law_proc_dt        date,
    law_submit_dt      date,
    proc_dt            date,
    propose_dt         date,
    id                 bigint       not null auto_increment,
    age                varchar(255),
    bill_id            varchar(255) not null,
    bill_name          varchar(255) not null,
    bill_no            varchar(255) not null,
    cmt_proc_result_cd varchar(255),
    committee          varchar(255),
    committee_id       varchar(255),
    detail_link        varchar(255),
    law_proc_result_cd varchar(255),
    proc_result        varchar(255),
    primary key (id)
) engine = InnoDB;

create table bill_summary
(
    bill_id  bigint,
    id       bigint not null auto_increment,
    category varchar(255),
    content  varchar(255),
    expected varchar(255),
    reason   varchar(255),
    primary key (id)
) engine = InnoDB;

create table congressman_bill
(
    bill_id        bigint,
    congressman_id bigint,
    id             bigint not null auto_increment,
    primary key (id)
) engine = InnoDB;

alter table bill
    add constraint unique_bill_id unique (bill_id);

alter table bill
    add constraint unique_bill_no unique (bill_no);

alter table bill_summary
    add constraint unique_bill_id_bill_summary unique (bill_id);

alter table bill_summary
    add constraint fk_bill_id_bill_summary
        foreign key (bill_id)
            references bill (id);

alter table congressman_bill
    add constraint fk_bill_id_congressman_bill
        foreign key (bill_id)
            references bill (id);

alter table congressman_bill
    add constraint fk_congressman_id_congressman_bill
        foreign key (congressman_id)
            references congressman (id);