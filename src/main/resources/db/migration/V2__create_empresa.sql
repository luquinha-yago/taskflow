CREATE TABLE public.empresa (
    codigo bigserial NOT NULL,
    razao_social text,
    nome_fantasia text,
    cnpj text,
    email text,
    telefone text,
    endereco text,
    data_cadastro date,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);