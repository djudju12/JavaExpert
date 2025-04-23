ATRIBUTO "Dor no peito"         TEXTO ( "Fraca", "Forte", "Inexistente" )
ATRIBUTO "Especialidade Médica" TEXTO ( "Pneumologia", "Cardiologia", "Neurologia" )
ATRIBUTO "Lapsos"               TEXTO ( "Constantes", "Inexistentes" )
ATRIBUTO "Lembranças"           TEXTO ( "Vagas", "Detalhadas" )
ATRIBUTO "Memória"              TEXTO ( "Falha", "Normal" )
ATRIBUTO "Movimentos"           TEXTO ( "Normais", "Lentos" )
ATRIBUTO "Respiração"           TEXTO ( "Normal", "Com dor" )
ATRIBUTO "Tosse"                TEXTO ( "Constante", "Fraca", "Inexistente" )

OBJETIVOS ("Especialidade Médica")

REGRA "Regra 1" (
    SE "Lembranças" = "Vagas"
    E  "Lapsos" = "Constantes"
    ENTAO "Memória" = "Falha"
)

REGRA "Regra 2" (
  SE "Lembranças" = "Detalhadas"
  E  "Lapsos" = "Inexistentes"
  ENTAO "Memória" = "Normal"
)

REGRA "Regra 3" (
  SE "Respiração" = "Normal"
  E  "Movimentos" = "Normais"
  ENTAO "Dor no peito" = "Inexistente"
)

REGRA "Regra 4" (
  SE "Respiração" = "Com dor"
  E  "Movimentos" = "Lentos"
  ENTAO "Dor no peito" = "Forte"
)

REGRA "Regra 5" (
  SE "Respiração" = "Com dor"
  E  "Movimentos" = "Normais"
  ENTAO "Dor no peito" = "Fraca"
)

REGRA "Regra 6" (
  SE "Memória" = "Normal"
  E  "Tosse" = "Constante"
  E  "Dor no peito" = "Fraca"
  ENTAO "Especialidade Médica" = "Pneumologia"
)

REGRA "Regra 7" (
  SE "Memória" = "Normal"
  E  "Tosse" = "Fraca"
  E  "Dor no peito" = "Forte"
  ENTAO "Especialidade Médica" = "Cardiologia"
)

REGRA "Regra 8" (
  SE "Memória" = "Falha"
  E  "Tosse" = "Inexistente"
  E  "Dor no peito" = "Inexistente"
  ENTAO "Especialidade Médica" = "Neurologia"
)