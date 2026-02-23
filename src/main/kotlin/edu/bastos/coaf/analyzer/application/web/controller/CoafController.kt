package edu.bastos.coaf.analyzer.application.controller

import edu.bastos.coaf.analyzer.domain.AnaliseRequest
import edu.bastos.coaf.analyzer.domain.SugestaoCoaf
import edu.bastos.coaf.analyzer.domain.service.AuditoriaService
import edu.bastos.coaf.analyzer.domain.service.CoafSugestaoService

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coaf")
class CoafController(
    private val coafService: CoafSugestaoService,
    private val auditoriaService: AuditoriaService
) {

    @PostMapping("/analisar")
    suspend fun analisar(
        @RequestBody request: AnaliseRequest
    ): ResponseEntity<SugestaoCoaf> {
        val sugestao = coafService.analisarParaCoaf(request)
        return ResponseEntity.ok(sugestao)
    }

    @PostMapping("/analisar/lote")
    suspend fun analisarLote(
        @RequestBody textos: List<String>
    ): ResponseEntity<List<SugestaoCoaf>> {
        val sugestoes = textos.map { texto ->
            coafService.analisarParaCoaf(AnaliseRequest(texto = texto))
        }
        return ResponseEntity.ok(sugestoes)
    }

    @PostMapping("/analisar/stream")
    fun analisarStream(
        @RequestBody textos: List<String>
    ): Flow<SugestaoCoaf> = flow {
        textos.forEach { texto ->
            emit(coafService.analisarParaCoaf(AnaliseRequest(texto = texto)))
        }
    }

    @GetMapping("/auditoria")
    fun listarAuditorias(): ResponseEntity<List<Any>> {
        return ResponseEntity.ok(auditoriaService.listarAuditorias())
    }

    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(
            mapOf(
                "status" to "UP",
                "service" to "coaf-analyzer",
                "timestamp" to java.time.Instant.now().toString()
            )
        )
    }
}