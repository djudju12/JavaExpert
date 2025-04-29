// Acadêmico: Jonathan Santos
// Data: 2025-04-27

// Sistema Especialista
// Decisão sobre Manejo de problema de saúde autolimitado: Dismenorreia


// -------------------- Atributos --------------------

// Início das dores
ATRIBUTO "dor_inicio" TEXTO ("2 dias antes da menstruação", "Sem relação temporal com a menstruação") // Pergunta

// Características dos sintomas
// TODO: 'caracteristica_dor' pode ser separada em duas regras?
ATRIBUTO "caracteristica_dor" TEXTO ("Cólica fora do período menstrual", "cólica durante o período menstrual", "unilateral") // Pergunta
ATRIBUTO "alteracao_do_local" TEXTO ("Sim", "Não") // Pergunta

ATRIBUTO "caracteristicas_incomuns" TEXTO ("Sim", "Não") // Intermediário

// Intensidade das dores
ATRIBUTO "intesidade_dor" TEXTO (
  "Dor leve a moderada",
  "Dor intensa que incapacita para a realização das atividades diárias",
  "Dor intensa que melhoram com anti-inflamatório"
) // Pergunta

// Dor associada com a menstruação
ATRIBUTO "dor_associada_menstruacao" TEXTO ("Sim", "Não") // Pergunta

// Outros sintomas (graves ou Não)
ATRIBUTO "outros_sintomas" TEXTO
(
  "Não", // TODO: Separar o 'Não em outra regra e nem perguntar dos sintomas em caso de 'Não'
  "Mal-estar",
  "Dor nas costas",
  "Dor na coxa",
  "Dor abdominal associada à diarreia, náuseas, vômitos ou queimação",
  "Dor periumbilical que irradia para o quadrante inferior direito",
  "Dor suprapúbica, associada à urgência urinária e/ou hematúria, independentemente da menstruação",
  "Cefaleia vascular frequente ou intensa",
  "Alterações do sistema urinário, como dor ao urinar, urgência miccional e/ou presença de sangue na urina",
  "Menorragia, oligomenorreia, sangramento no período intermenstrual"
) // Pergunta

ATRIBUTO "sintomas_graves" TEXTO ("Sim", "Não")

// Primeira dismenorreia
ATRIBUTO "primeira_dismenorreia" TEXTO (
  "Início dos sinais/sintomas após 25 anos de idade (30 a 40 anos)",
  "Entre 12 e 13 anos (cerca de 6 à 12 meses após menarca)"
) // Pergunta

// Historico Clinico
ATRIBUTO "historico_clinico" TEXTO (
  "Pacientes com hipertensão",
  "Insuficiência cardíaca",
  "insuficiência renal",
  "Doença gastrointestinal",
  "Asma",
  "Bronquite",
  "Nenhuma"
) // Pergunta

ATRIBUTO "possui_historico_clinico_relevante" TEXTO ("Sim", "Não")

// Historico Farmacoterapeutico
ATRIBUTO "historico_farmacoterapeutico" TEXTO (
  "Tratamentos prévios",
  "Tratamentos concomitantes com falha terapêutica",
  "Tratamentos com reações adversas",
  "Nenhum"
) // Pergunta

ATRIBUTO "possui_historico_farmacoterapeutico_relevante" TEXTO ("Sim", "Não")


// -------------------- Objetivos --------------------
ATRIBUTO "decisao" TEXTO ("encaminhar", "autodelimitado")
OBJETIVOS ("decisao")


// -------------------- Regras Intermediárias --------------------

REGRA "caracteristicas_sao_incomuns" (
  SE
       "caracteristica_dor" = "suprapúbica fora do período menstrual"
    OU "caracteristica_dor" = "unilateral"
    OU "alteracao_do_local" = "Sim"
  ENTAO
    "caracteristicas_incomuns" = "Sim"
)

REGRA "caracteristicas_nao_sao_incomuns" (
  SE
      "caracteristica_dor" <> "suprapúbica fora do período menstrual"
    E "alteracao_do_local" <> "Sim"
    E "caracteristica_dor" <> "unilateral"
  ENTAO
      "caracteristicas_incomuns" = "Não"
)

REGRA "sintomas_sao_graves" (
  SE
       "outros_sintomas" = "Dor abdominal associada à diarreia, náuseas, vômitos ou queimação"
    OU "outros_sintomas" = "Dor periumbilical que irradia para o quadrante inferior direito"
    OU "outros_sintomas" = "Dor suprapúbica, associada à urgência urinária e/ou hematúria, independentemente da menstruação"
    OU "outros_sintomas" = "Cefaleia vascular frequente ou intensa"
    OU "outros_sintomas" = "Alterações do sistema urinário, como dor ao urinar, urgência miccional e/ou presença de sangue na urina"
    OU "outros_sintomas" = "Menorragia, oligomenorreia, sangramento no período intermenstrual"
  ENTAO
      "sintomas_graves" = "Sim"
)

REGRA "sintomas_nao_sao_graves" (
  SE
       "outros_sintomas" <> "Dor abdominal associada à diarreia, náuseas, vômitos ou queimação"
    E  "outros_sintomas" <> "Dor periumbilical que irradia para o quadrante inferior direito"
    E  "outros_sintomas" <> "Dor suprapúbica, associada à urgência urinária e/ou hematúria, independentemente da menstruação"
    E  "outros_sintomas" <> "Cefaleia vascular frequente ou intensa"
    E  "outros_sintomas" <> "Alterações do sistema urinário, como dor ao urinar, urgência miccional e/ou presença de sangue na urina"
    E  "outros_sintomas" <> "Menorragia, oligomenorreia, sangramento no período intermenstrual"
  ENTAO
      "sintomas_graves" = "nao"
)

REGRA "historico_clinico_relavante" (
  SE
       "historico_clinico" = "Hipertensão"
    OU "historico_clinico" = "Insuficiência cardíaca"
    OU "historico_clinico" = "insuficiência renal"
    OU "historico_clinico" = "Doença gastrointestinal"
    OU "historico_clinico" = "Asma"
    OU "historico_clinico" = "Bronquite"
  ENTAO
    "possui_historico_clinico_relevante" = "Sim"
)

REGRA "historico_clinico_nao_relavante" (
  SE
      "historico_clinico" <> "Hipertensão"
    E "historico_clinico" <> "Insuficiência cardíaca"
    E "historico_clinico" <> "insuficiência renal"
    E "historico_clinico" <> "Doença gastrointestinal"
    E "historico_clinico" <> "Asma"
    E "historico_clinico" <> "Bronquite"
  ENTAO
    "possui_historico_clinico_relevante" = "Não"
)

REGRA "possui_historico_farmacoterapeutico_relevante" (
  SE
       "historico_farmacoterapeutico" = "Tratamentos prévios"
    OU "historico_farmacoterapeutico" = "Tratamentos concomitantes com falha terapêutica"
    OU "historico_farmacoterapeutico" = "Tratamentos com reações adversas"
  ENTAO
    "possui_historico_farmacoterapeutico_relevante" = "Sim"
)

REGRA "possui_historico_farmacoteraputico_nao_relevante" (
  SE
      "historico_farmacoterapeutico" <> "Tratamentos prévios"
    E "historico_farmacoterapeutico" <> "Tratamentos concomitantes com falha terapêutica"
    E "historico_farmacoterapeutico" <> "Tratamentos com reações adversas"
  ENTAO
    "possui_historico_farmacoterapeutico_relevante" = "Não"
)

// -------------------- Regras conclusivas --------------------

REGRA "deve_encaminhar" (
  SE
       "dor_inicio" <> "2 dias antes da menstruação"
    OU "caracteristicas_incomuns" = "Sim"
    OU "intesidade_dor" = "Dor intensa que incapacita para a realização das atividades diárias"
    OU "dor_associada_menstruacao" = "Não"
    OU "sintomas_graves" = "Sim"
    OU "primeira_dismenorreia" = "Início dos sinais/sintomas após 25 anos de idade (30 a 40 anos)"
    OU "possui_historico_clinico_relevante" = "Sim"
    OU "possui_historico_farmacoterapeutico_relevante" = "Sim"
  ENTAO
      "decisao" = "encaminhar"
)

REGRA "problema_autodelimitado" (
  SE
      "dor_inicio" = "2 dias antes da menstruação"
    E "caracteristicas_incomuns" = "Não"
    E "intesidade_dor" <> "Dor intensa que incapacita para a realização das atividades diárias"
    E "dor_associada_menstruacao" = "Sim"
    E "sintomas_graves" = "Não"
    E "primeira_dismenorreia" <> "Início dos sinais/sintomas após 25 anos de idade (30 a 40 anos)"
    E "possui_historico_clinico_relevante" = "Não"
    E "possui_historico_farmacoterapeutico_relevante" = "Não"
  ENTAO
      "decisao" = "autodelimitado"
)