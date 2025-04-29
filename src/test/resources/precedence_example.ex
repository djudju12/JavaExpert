ATRIBUTO "AA" TEXTO ( "A", "B" )

ATRIBUTO "AB" TEXTO ( "C", "D" )

ATRIBUTO "AC" TEXTO ( "E", "F" )

ATRIBUTO "OO" TEXTO ( "X", "Y" )

OBJETIVOS ("OO")

// AA = A AB = C

// (A && B) || (C && D)
REGRA "R1" (
  SE
     ("AA" = "A" E "AB" = "C")
  OU ("AA" = "B" E "AB" = "D")
  ENTAO "OO" = "X"
)

// (((A && B) || C) && D)
REGRA "R2" (
  SE
     "AA" = "A" E "AB" = "C"
  OU "AA" = "B" E "AB" = "D"
  ENTAO "OO" = "Y"
)
