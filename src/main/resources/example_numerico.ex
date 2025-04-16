ATRIBUTO "idade" NUMERICO
ATRIBUTO "sexo" ("feminino", "masculino")
ATRIBUTO "presente" (
  "fralda",
  "boneca", "carrinho",
  "memoria_ram", "iphone",
  "flores", "cerveja",
  "avental_vovô", "tapete_ioga"
)

OBJETIVOS ("presente")

REGRA "NENE" (
  SE    "idade" < 3
  ENTAO "presente" = "fralda"
)

REGRA "MENINO" (
  SE    "idade" >= 3
  E     "idade" < 13
  E     "sexo" = "masculino"
  ENTAO "presente" = "carrinho"
)

REGRA "MENINA" (
  SE    "idade" >= 3
  E     "idade" < 13
  E     "sexo" = "feminino"
  ENTAO "presente" = "boneca"
)

REGRA "ADOLESCENTE_MASC" (
  SE "idade" >= 13
  E  "idade" < 20
  E  "sexo" = "masculino"
  ENTAO "presente" = "memoria_ram"
)

REGRA "ADOLESCENTE_FEM" (
  SE "idade" >= 13
  E  "idade" < 20
  E  "sexo" = "feminino"
  ENTAO "presente" = "iphone"
)

REGRA "ADULTO_MASC" (
  SE "idade" >= 20
  E  "idade" < 65
  E  "sexo" = "masculino"
  ENTAO "presente" = "cerveja"
)

REGRA "ADULTO_FEM" (
  SE "idade" >= 20
  E  "idade" < 65
  E  "sexo" = "feminino"
  ENTAO "presente" = "flores"
)

REGRA "IDOSO_MASC" (
  SE  "idade" >= 65
  E  "sexo" = "masculino"
  ENTAO "presente" = "avental_vovô"
)

REGRA "IDOSO_FEM" (
  SE  "idade" >= 65
  E  "sexo" = "feminino"
  ENTAO "presente" = "tapete_ioga"
)
