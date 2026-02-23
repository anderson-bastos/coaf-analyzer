package edu.bastos.coaf.analyzer.domain.service

import edu.bastos.coaf.analyzer.domain.Decisao
import edu.bastos.coaf.analyzer.domain.SugestaoCoaf
import edu.bastos.coaf.analyzer.domain.rule.*
import org.springframework.stereotype.Component

@Component
class CoafRulesValidator(
    private val highValueRule: HighValueRule,
    private val structuringRule: StructuringRule,
    private val profileIncompatibilityRule: ProfileIncompatibilityRule,
    private val taxHaven: TaxHaven
) {

    private val rules: List<CoafRule> by lazy {
        listOf(
            highValueRule,
            structuringRule,
            profileIncompatibilityRule,
            taxHaven
        )
    }

    fun aplicarRegras(sugestaoIA: SugestaoCoaf, textoOriginal: String): SugestaoCoaf {
        // Se IA já recomendou comunicar com alta confiança, mantém
        if (sugestaoIA.decisao == Decisao.COMUNICAR && sugestaoIA.confianca > 0.8) {
            return sugestaoIA
        }

        // Aplica regras explícitas
        val regrasAcionadas = rules.filter { it.appliesTo(textoOriginal) }

        return if (regrasAcionadas.isNotEmpty()) {
            val justificativaCompleta = buildString {
                appendLine("Regras do COAF acionadas:")
                regrasAcionadas.forEachIndexed { index, regra ->
                    appendLine("${index + 1}. ${regra.description}")
                }
            }

            sugestaoIA.copy(
                decisao = Decisao.COMUNICAR,
                justificativa = justificativaCompleta,
                enquadramentoLegal = regrasAcionadas.joinToString("; ") { it.basisLegal },
                confianca = maxOf(sugestaoIA.confianca, 0.95),
                alertas = sugestaoIA.alertas + regrasAcionadas.map { it.alert }
            )
        } else {
            sugestaoIA
        }
    }
}