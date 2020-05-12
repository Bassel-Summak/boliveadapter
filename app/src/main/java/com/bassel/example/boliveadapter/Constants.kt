package com.bassel.example.boliveadapter

class Constants {

    class URLS {

        companion object{

            const val BASE_URL = "http://dummy.restapiexample.com/api/v1/"
            const val ERROR_URL = "employee/0"
            const val DATA_URL = "employee/1"
        }
    }

    open class TOOLS{

        companion object{

            fun FormatStringToJson(text: String): String? {
                val json = StringBuilder()
                var indentString = ""
                for (i in 0 until text.length) {
                    val letter = text[i]
                    when (letter) {
                        '{', '[' -> {
                            json.append(
                                """
                                
                                $indentString$letter
                                
                                """.trimIndent()
                            )
                            indentString = indentString + "\t"
                            json.append(indentString)
                        }
                        '}', ']' -> {
                            indentString = indentString.replaceFirst("\t".toRegex(), "")
                            json.append(
                                """
                                
                                $indentString$letter
                                """.trimIndent()
                            )
                        }
                        ',' -> json.append(
                            """
                            $letter
                            $indentString
                            """.trimIndent()
                        )
                        else -> json.append(letter)
                    }
                }
                return json.toString()
            }


        }

    }


}