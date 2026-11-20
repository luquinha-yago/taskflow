CREATE TABLE public.departamento (
    codigo bigserial NOT NULL,
    nome text,
    descricao text,
    funcionario_coordenador_id bigint,
    empresa_id bigint,
    data_criacao date,
    status text DEFAULT 'ATIVO',
    PRIMARY KEY (codigo)
);

ALTER TABLE public.departamento
    ADD FOREIGN KEY (codigo_funcionario_coordenador) REFERENCES public.funcionario (codigo),
    ADD FOREIGN KEY (codigo_empresa) REFERENCES public.empresa (codigo);
