package edu.bastos.coaf.analyzer.domain

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.Instant

enum class Decisao {
    COMUNICAR,
    NAO_COMUNICAR,
    REVISAO_MANUAL
}

enum class Prioridade {
    BAIXA, NORMAL, ALTA, URGENTE
}

data class SugestaoCoaf(
    val decisao: Decisao,
    val justificativa: String,
    val enquadramentoLegal: String,
    val fundamentacaoTecnica: String,
    val confianca: Double,
    val alertas: List<String> = emptyList(),
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    val timestamp: Instant = Instant.now()
)

data class AnaliseRequest(
    val texto: String,
    val clienteId: String? = null,
    val prioridade: Prioridade = Prioridade.NORMAL
)

data class AnaliseAuditoria(
    val id: String? = null,
    val clienteId: String?,
    val textoAnalisado: String,
    val sugestao: SugestaoCoaf,
    val usuario: String = "SISTEMA",
    val timestamp: Instant = Instant.now()
)