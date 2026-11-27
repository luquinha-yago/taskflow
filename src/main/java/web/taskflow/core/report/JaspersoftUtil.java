package web.taskflow.core.report;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@Slf4j
@Component
public class JaspersoftUtil {

    private final DataSource dataSource;

    public JaspersoftUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public byte[] gerarRelatorio(String arquivoJasper) {
        return gerarRelatorio(arquivoJasper, null);
    }

    public byte[] gerarRelatorio(String arquivoJasper, Map<String, Object> parametros) {
        try (Connection conexao = dataSource.getConnection()) {
            ClassPathResource cpr = new ClassPathResource(arquivoJasper);
            try (InputStream arquivo = cpr.getInputStream()) {
                String urlRelatorio = cpr.getURL().toString();
                String diretorioRelatorios = urlRelatorio.substring(0, urlRelatorio.lastIndexOf("/") + 1);
                
                if (parametros == null) {
                    parametros = new HashMap<>();
                }
                parametros.put("SUBREPORT_DIR", diretorioRelatorios);
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(arquivo, parametros, conexao);
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }
        } catch (JRException | IOException | SQLException e) {
            log.error("Erro na geração do relatório PDF ({}): ", arquivoJasper, e);
            throw new RuntimeException("Não foi possível gerar o relatório.", e);
        }
    }
}