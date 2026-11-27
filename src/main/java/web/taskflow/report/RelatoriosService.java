package web.taskflow.report;

import org.springframework.stereotype.Service;
import web.taskflow.core.report.JaspersoftUtil;

@Service
public class RelatoriosService {
    private final JaspersoftUtil jaspersoftUtil;

    public RelatoriosService(JaspersoftUtil jaspersoftUtil) {
        this.jaspersoftUtil = jaspersoftUtil;
    }

    public byte[] gerarRelatorioTodasVacinasLotes() {
        return jaspersoftUtil.gerarRelatorio("/relatorios/TodasVacinasCompleto.jasper");
    }
}
