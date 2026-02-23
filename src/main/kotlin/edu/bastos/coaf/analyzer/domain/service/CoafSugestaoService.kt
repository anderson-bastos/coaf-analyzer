package edu.bastos.coaf.analyzer.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import edu.bastos.coaf.analyzer.domain.AnaliseRequest
import edu.bastos.coaf.analyzer.domain.Decisao
import edu.bastos.coaf.analyzer.domain.SugestaoCoaf
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.openai.OpenAiChatClient
import org.springframework.stereotype.Service

@Service
class CoafSugestaoService(
    private val chatModel: OpenAiChatClient,  // Não é ChatClient, é ChatModel
    private val rulesValidator: CoafRulesValidator,
    private val auditoriaService: AuditoriaService,
    private val objectMapper: ObjectMapper
) {

    companion object {
        private val log = LoggerFactory.getLogger(CoafSugestaoService::class.java)

        private const val SYSTEM_PROMPT = """
            Você é um especialista em compliance e prevenção à lavagem de dinheiro.
            Sua função é analisar transações e situações financeiras para determinar
            se devem ser comunicadas ao COAF (Conselho de Controle de Atividades Financeiras).
            
            Baseie suas análises nas seguintes regulamentações:
            - Lei 9.613/98 (Lei de Lavagem de Dinheiro)
            - Circular BACEN 3.978/2020
            - Carta Circular BACEN 4.001/2020
            
            Seja conservador: em caso de dúvida, recomende comunicação.
            
            Responda SEMPRE em formato JSON válido.
        """
    }

    suspend fun analisarParaCoaf(request: AnaliseRequest): SugestaoCoaf {
        log.info("Analisando texto para COAF: ${request.clienteId ?: "sem ID"}")

        return try {
            val userPrompt = buildUserPrompt(request.texto)

            // Cria as mensagens
            val systemMessage = SystemMessage(SYSTEM_PROMPT)
            val userMessage = UserMessage(userPrompt)

            // Cria o prompt com as mensagens
            val prompt = Prompt(listOf(systemMessage, userMessage))

            // Chama o modelo - sem sintaxe fluente
            val chatResponse = chatModel.call(prompt)
            val respostaIA = chatResponse.result.output.content

            log.debug("Resposta da IA: $respostaIA")

            // Converte para objeto
            val sugestaoIA: SugestaoCoaf = objectMapper.readValue(respostaIA)

            // Aplica regras do COAF
            val sugestaoFinal = rulesValidator.aplicarRegras(sugestaoIA, request.texto)

            // Registra para auditoria
            auditoriaService.registrarAnalise(request, sugestaoFinal)

            sugestaoFinal

        } catch (e: Exception) {
            log.error("Erro na análise COAF", e)
            SugestaoCoaf(
                decisao = Decisao.REVISAO_MANUAL,
                justificativa = "Erro no processamento automático: ${e.message}",
                enquadramentoLegal = "N/A",
                fundamentacaoTecnica = "Falha na análise por IA",
                confianca = 0.0,
                alertas = listOf("ERRO_TECNICO", "REVISAO_MANUAL_NECESSARIA")
            )
        }
    }

    private fun buildUserPrompt(texto: String): String {
        return """
            Analise a seguinte situação para fins de comunicação ao COAF:
            
            $texto
            
            Considere os seguintes aspectos:
            1. Valores envolvidos (acima de R$ 50 mil em espécie é comunicável)
            2. Incompatibilidade com perfil do cliente
            3. Operações fragmentadas (structuring)
            4. Origem suspeita dos recursos
            5. Envolvimento com paraísos fiscais
            6. Setores de alto risco (joalherias, imobiliárias, etc.)
            
            Forneça uma resposta JSON com:
            - decisao: "COMUNICAR" ou "NAO_COMUNICAR"
            - justificativa: texto explicativo
            - enquadramentoLegal: artigos das leis/normas aplicáveis
            - fundamentacaoTecnica: análise detalhada
            - confianca: número de 0.0 a 1.0
            - alertas: lista de pontos suspeitos identificados
            
            Responda apenas com o JSON, sem texto adicional.
        """.trimIndent()
    }
}