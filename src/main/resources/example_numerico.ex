ATRIBUTO "idade" NUMERICO
ATRIBUTO "sexo" TEXTO ("feminino", "masculino")
ATRIBUTO "fase_da_vida" TEXTO ("nene", "crianca", "adolescente", "adulto", "idoso")
ATRIBUTO "presente" TEXTO (
  "fralda",
  "boneca", "carrinho",
  "memoria_ram", "iphone",
  "flores", "cerveja",
  "avental_vovô", "tapete_ioga"
)

OBJETIVOS ("presente")

REGRA "NENE" (
  SE    "idade" < 3
  ENTAO "fase_da_vida" = "nene"
)

REGRA "CRIANCA" (
  SE    "idade" >= 3 E "idade" < 13
  ENTAO "fase_da_vida" = "crianca"
)

REGRA "ADOLESCENTE" (
  SE    "idade" >= 13 E "idade" < 20
  ENTAO "fase_da_vida" = "adolescente"
)

REGRA "ADULTO" (
  SE    "idade" >= 20 E "idade" < 65
  ENTAO "fase_da_vida" = "adulto"
)

REGRA "IDOSO" (
  SE    "idade" >= 65
  ENTAO "fase_da_vida" = "idoso"
)

REGRA "PRESENTE_NENE" (
  SE    "fase_da_vida" = "nene"
  ENTAO "presente" = "fralda"
)

REGRA "PRESENTE_MENINO" (
  SE    "fase_da_vida" = "crianca"
  E     "sexo" = "masculino"
  ENTAO "presente" = "carrinho"
)

REGRA "PRESENTE_MENINA" (
  SE    "fase_da_vida" = "crianca"
  E     "sexo" = "feminino"
  ENTAO "presente" = "boneca"
)

REGRA "PRESENTE_ADOLESCENTE_MASC" (
  SE    "fase_da_vida" = "adolescente"
  E     "sexo" = "masculino"
  ENTAO "presente" = "memoria_ram"
)

REGRA "PRESENTE_ADOLESCENTE_FEM" (
  SE    "fase_da_vida" = "adolescente"
  E     "sexo" = "feminino"
  ENTAO "presente" = "iphone"
)

REGRA "PRESENTE_ADULTO_MASC" (
  SE    "fase_da_vida" = "adulto"
  E     "sexo" = "masculino"
  ENTAO "presente" = "cerveja"
)

REGRA "PRESENTE_ADULTO_FEM" (
  SE    "fase_da_vida" = "adulto"
  E     "sexo" = "feminino"
  ENTAO "presente" = "flores"
)

REGRA "PRESENTE_IDOSO_MASC" (
  SE    "fase_da_vida" = "idoso"
  E     "sexo" = "masculino"
  ENTAO "presente" = "avental_vovô"
)

REGRA "PRESENTE_IDOSO_FEM" (
  SE    "fase_da_vida" = "idoso"
  E     "sexo" = "feminino"
  ENTAO "presente" = "tapete_ioga"
)
