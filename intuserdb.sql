CREATE TABLE IF NOT EXISTS public.users
(
    user_id uuid NOT NULL,
    email character varying(255) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default",
    questions_completed jsonb,
    role character varying(255) COLLATE pg_catalog."default",
    user_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (user_id),
    CONSTRAINT uk_6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email),
    CONSTRAINT users_role_check CHECK (role::text = ANY (ARRAY['USER'::character varying, 'ADMIN'::character varying]::text[]))
);


insert into public.users(
	user_id, email,password, role, user_name)
VALUES (gen_random_uuid (),'amba.admin@gmail.com',
'$2a$10$ljHkolQ17dDoJLY5MGspeOOvQx8.z6L5YUOSKbJCv2nu3b1/ASCNK', 'ADMIN','amba');