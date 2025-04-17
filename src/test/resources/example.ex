ATRIBUTO "materia_prima"       ("alta", "media", "baixa")
ATRIBUTO "processo_fabricacao" ("otimo", "aceitavel", "ruim")
ATRIBUTO "controle_qualidade"  ("rigoroso", "moderado", "fraco")
ATRIBUTO "durabilidade"        ("alta", "media", "baixa")
ATRIBUTO "acabamento"          ("excelente", "regular", "defeituoso")
ATRIBUTO "satisfacao_cliente"  ("alta", "media", "baixa")
ATRIBUTO "qualidade_final"     ("aceitavel" , "rejeitavel")

OBJETIVOS ("qualidade_final")

REGRA "R1" (
  SE    "materia_prima" = "baixa"
  OU    "processo_fabricacao" = "ruim"
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