package edu.bastos.coaf.analyzer.domain.service

import edu.bastos.coaf.analyzer.domain.AnaliseAuditoria
import edu.bastos.coaf.analyzer.domain.AnaliseRequest
import edu.bastos.coaf.analyzer.domain.SugestaoCoaf
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedQueue

@Service
class AuditoriaService {

    private val log = LoggerFactory.getLogger(AuditoriaService::class.java)

    // Em memória para exemplo (em produção, use MongoDB, PostgreSQL, etc.)
    private val auditoriaQueue = ConcurrentLinkedQueue<AnaliseAuditoria>()

    suspend fun registrarAnalise(request: AnaliseRequest, sugestao: SugestaoCoaf) {
        val auditoria = AnaliseAuditoria(
            clienteId = request.clienteId,
            textoAnalisado = request.texto,
            sugestao = sugestao,
            timestamp = Instant.now()
        )

        try {
            // Em produção: salvar no banco de dados
            auditoriaQueue.offer(auditoria)
            log.info("Auditoria registrada: ${auditoria.id}")

            // Log para compliance
            log.info("""
                === ANÁLISE COAF ===
                Cliente: ${auditoria.clienteId ?: "N/A"}
                Decisão: ${auditoria.sugestao.decisao}
                Confiança: ${auditoria.sugestao.confianca}
                Alertas: ${auditoria.sugestao.alertas}
                ===================
            """.trimIndent())

        } catch (e: Exception) {
            log.error("Erro ao salvar auditoria", e)
        }
    }

    fun listarAuditorias(): List<AnaliseAuditoria> {
        return auditoriaQueue.toList()
    }
}