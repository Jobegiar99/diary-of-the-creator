package com.jobegiar99.dotc.helperClasses

class EncoderDecoder {
    val entrySeparator: String = "<DotC.Entry.Separator>"
    val categorySeparator: String = "<DotC.Category.Separator>"

    fun decodeCategory(encodedCategory: String): List<String>{
        return encodedCategory.split(this.categorySeparator)
    }

    fun encodeCategory(categoryElements: Array<String>): String{
        return categoryElements.joinToString(separator = this.categorySeparator)
    }

    fun decodeEntry(encodedEntry: String): List<String>{
        return encodedEntry.split(this.entrySeparator)
    }

    fun encodeEntry(entryContent: Array<String>): String{
        return entryContent.joinToString(separator = this.entrySeparator)
    }
}