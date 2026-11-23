ALTER TABLE public.funcionario
    ADD COLUMN codigo_empresa BIGINT,
    ADD COLUMN codigo_departamento BIGINT;

ALTER TABLE public.funcionario
    ADD CONSTRAINT fk_funcionario_empresa
        FOREIGN KEY (codigo_empresa)
        REFERENCES public.empresa (codigo),
    ADD CONSTRAINT fk_funcionario_departamento
        FOREIGN KEY (codigo_departamento)
        REFERENCES public.departamento (codigo);