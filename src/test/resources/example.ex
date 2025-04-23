ATRIBUTO "materia_prima"       TEXTO ("alta", "media", "baixa") // comentario
// comentario
ATRIBUTO "processo_fabricacao" TEXTO ("otimo", "aceitavel", "ruim")
ATRIBUTO "controle_qualidade"  TEXTO ("rigoroso", "moderado", "fraco")
ATRIBUTO "durabilidade"        TEXTO ("alta", "media", "baixa")
ATRIBUTO "acabamento"          TEXTO ("excelente", "regular", "defeituoso")
ATRIBUTO "satisfacao_cliente"  TEXTO ("alta", "media", "baixa")
ATRIBUTO "qualidade_final"     TEXTO ("aceitavel" , "rejeitavel")

OBJETIVOS ("qualidade_final")

REGRA "R1" (//comentario
//comentario
  SE    "materia_prima" = "baixa"
  OU    "processo_fabricacao" = "ruim" // comentario
  ENTAO "durabilidade" = "baixa"
)

REGRA "R2" (
  SE    "materia_prima" = "alta"
  E     "processo_fabricacao" = "otimo"
  ENTAO "durabilidade" = "alta"
)

REGRA "R3" (
  SE     "controle_qualidade" = "rigoroso"
  E      "acabamento" = "excelente"
  ENTAO  "satisfacao_cliente" = "alta"
)

REGRA "R4" (
  SE    "controle_qualidade" = "fraco"
  OU    "acabamento" = "defeituoso"
  ENTAO "satisfacao_cliente" = "baixa"
)

REGRA "R5" (
  SE    "durabilidade" = "baixa"
  OU    "satisfacao_cliente" = "baixa"
  ENTAO "qualidade_final" = "rejeitavel"
)

REGRA "R6" (
  SE    "durabilidade" = "alta"
  E     "satisfacao_cliente" = "alta"
  ENTAO "qualidade_final" = "aceitavel"
)
//comentario