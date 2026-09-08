-- Until now the only thing recorded about a user's privileges was the boolean is_admin,
-- which cannot distinguish an accountant from a read-only user: both are simply "not an
-- admin". The Read-only role has existed in Auth0 from the start but was unreachable,
-- because nothing stored or requested it.
--
-- Storing the role name makes the distinction visible in the application (the user list
-- can show it) and lets an administrator choose it at creation time. Auth0 remains the
-- authority on what a role actually permits; this column records which role was assigned.
alter table login_user add column role varchar(50);

-- Backfill existing rows from the flag they were created with. Every current user was
-- created through a path that could only produce one of these two roles.
update login_user set role = case when is_admin then 'Admin' else 'RegularUser' end;

alter table login_user alter column role set not null;
