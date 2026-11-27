package web.taskflow.report;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j 
public class RelatoriosController {
    private final RelatoriosService relatorioService;

    public RelatoriosController(RelatoriosService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/relatorios/vacinascompleto")
	public ResponseEntity<byte[]> gerarRelatorioComplexoTodasVacinasLotes() {
		log.debug("Gerando relatório complexo de todas as vacinas com lotes");
		
		byte[] relatorio = relatorioService.gerarRelatorioTodasVacinasLotes();
		
		log.debug("Relatório complexo de todas as vacinas com lotes");
		log.debug("Retornando o relatório complexo de todas as vacinas com lotes");
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=VacinasComLotes.pdf")
				.body(relatorio);
	}
}
