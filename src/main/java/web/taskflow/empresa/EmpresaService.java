package web.taskflow.empresa;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import web.taskflow.common.enums.Status;
import web.taskflow.common.exception.EntidadeNaoEncontradaException;
import web.taskflow.empresa.dto.EmpresaCreateRequest;
import web.taskflow.empresa.dto.EmpresaResponse;
import web.taskflow.empresa.dto.EmpresaSearchRequest;
import web.taskflow.empresa.dto.EmpresaUpdateRequest;

@Service
@Transactional(readOnly = true)
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    public EmpresaService(EmpresaRepository empresaRepository){
        this.empresaRepository = empresaRepository;
    }

    @Transactional
    public void salvar(EmpresaCreateRequest form) {
        empresaRepository.save(toEntity(form));
    }

    public Page<EmpresaResponse> pesquisar(
           EmpresaSearchRequest filtro,
           Pageable pageable) {

        validarPeriodo(filtro);

        return empresaRepository
                .findAll(
                    EmpresaSpecifications.comFiltros(filtro),
                    pageable
                )
                .map(this::toResponse);
    }

    private void validarPeriodo(EmpresaSearchRequest filtro) {

        if (filtro == null) {
            return;
        }

        if (filtro.dataCadastroInicial() != null
                && filtro.dataCadastroFinal() != null
                && filtro.dataCadastroInicial()
                    .isAfter(filtro.dataCadastroFinal())) {

            throw new IllegalArgumentException(
                "A data inicial não pode ser posterior à data final."
            );
        }
    }

    public List<EmpresaResponse> buscarPeloNome(String nome) {
        return empresaRepository.findTop10ByNomeFantasiaContainingIgnoreCaseAndStatusOrderByNomeFantasiaAsc(nome, Status.ATIVO).stream()
            .map(this::toResponse)
            .toList();
    }

    public EmpresaUpdateRequest buscarParaEdicao(Long codigo) {
        Empresa empresa = empresaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encontrada"));
        return toUpdateRequest(empresa);
    }

    @Transactional 
    public void atualizar(Long codigo, EmpresaUpdateRequest request) {
        Empresa empresa = empresaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encontrada"));
        empresa.setRazaoSocial(request.razaoSocial());
        empresa.setNomeFantasia(request.nomeFantasia());
        empresa.setCnpj(request.cnpj());
        empresa.setEmail(request.email());
        empresa.setTelefone(request.telefone());
        empresa.setEndereco(request.endereco());
        empresaRepository.save(empresa);
    }

    @Transactional 
    public void remover(Long codigo) {
        Empresa empresa = empresaRepository.findByCodigoAndStatus(codigo, Status.ATIVO)
        .orElseThrow(() -> new EntidadeNaoEncontradaException("Empresa não encontrada"));
        empresa.setStatus(Status.INATIVO);
        empresaRepository.save(empresa);
    }

    private Empresa toEntity(EmpresaCreateRequest request) {
        Empresa empresa = new Empresa();
        empresa.setRazaoSocial(request.razaoSocial());
        empresa.setNomeFantasia(request.nomeFantasia());
        empresa.setCnpj(request.cnpj());
        empresa.setEmail(request.email());
        empresa.setTelefone(request.telefone());
        empresa.setEndereco(request.endereco());
        empresa.setDataCadastro(LocalDate.now());
        return empresa;
    }

    private EmpresaResponse toResponse(Empresa empresa) {

        return new EmpresaResponse(
            empresa.getCodigo(),
            empresa.getRazaoSocial(),
            empresa.getNomeFantasia(),
            empresa.getCnpj(),
            empresa.getEmail(),
            empresa.getTelefone(),
            empresa.getEndereco(),
            empresa.getDataCadastro()
        );
    }

    private EmpresaUpdateRequest toUpdateRequest(Empresa empresa) {
        return new EmpresaUpdateRequest(empresa.getRazaoSocial(),
                                        empresa.getNomeFantasia(),
                                        empresa.getCnpj(),
                                        empresa.getEmail(),
                                        empresa.getTelefone(),
                                        empresa.getEndereco());
    }
}