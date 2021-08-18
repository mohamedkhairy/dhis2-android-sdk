/*
 *  Copyright (c) 2004-2021, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.datavalue.calculator

import android.util.Log
import dagger.Reusable
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.datavalue.DataValue
import org.hisp.dhis.android.core.datavalue.DataValueTableInfo
import org.hisp.dhis.android.core.datavalue.internal.DataValueStore
import org.hisp.dhis.android.core.user.UserCredentialsTableInfo
import java.util.*
import javax.inject.Inject

/**
 * This calculator must accept several parameters in a builder pattern and return a result.
 * If the same parameter is provided several times, the evaluator must use the last one and ignore the previous ones.
 *
 * You can inject the class [DataValueStore] with Dagger. This class
 * can be used to retrieve the values from the database using the "select" methods.
 *
 * If you want to build custom "where" statements you can use the class [WhereClauseBuilder].
 * Additionally, you can know the column names looking at this [DataValueTableInfo.Columns].
 *
 */
@Reusable
class DataValueCalculator @Inject constructor(private val dataValueStore: DataValueStore): CalculatorOperation() {

    private var listOfDataValue: MutableList<DataValue>? = null
    private var aggregationTypes: AggregationType = AggregationType.SUM


    /**
     * Filter the dataValues whose dataElement match exactly this parameter
     */
    fun withDataElement(dataElement: String): DataValueCalculator =
        apply {

            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(DataValueTableInfo.Columns.DATA_ELEMENT, dataElement)
                .build()

            listOfDataValue?.let{ list ->
                list.filter { it.dataElement() == dataElement }.applyChanges()
            } ?:
            dataValueStore.selectWhere(whereClause).applyChanges()

        }
    /**
     * Filter the dataValues whose period match exactly this parameter
     */
    fun withPeriod(period: String): DataValueCalculator =
        apply {
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(DataValueTableInfo.Columns.PERIOD, period)
                .build()

            listOfDataValue?.let{ list ->
                val result = list.filter { it.period() == period }
                if (result.isNotEmpty()){
                    result.applyChanges()
                }else
                    dataValueStore.selectWhere(whereClause).applyChanges()
            } ?:
            dataValueStore.selectWhere(whereClause).applyChanges()

        }

    /**
     * Filter the dataValues whose categoryOptionCombo match exactly this parameter
     */
    fun withCategoryOptionCombo(coc: String): DataValueCalculator =
        apply {
            val whereClause = WhereClauseBuilder()
                .appendKeyStringValue(DataValueTableInfo.Columns.CATEGORY_OPTION_COMBO, coc)
                .build()

            listOfDataValue?.let{ list ->
                list.filter { it.categoryOptionCombo() == coc }.applyChanges()
            } ?:
            dataValueStore.selectWhere(whereClause).applyChanges()
        }

    /**
     * Filter the dataValues whose created date is after this parameter.
     */
    fun withCreatedAfter(date: Date): DataValueCalculator =
        apply {

            val dateString = DateUtils.DATE_FORMAT.format(date)

            val whereClause = WhereClauseBuilder()
                .appendKeyGreaterThanStringValue(DataValueTableInfo.Columns.CREATED, dateString)
                .build()

            listOfDataValue?.let{ list ->
                list.filter { it.created()!!.after(date)}.applyChanges()
            } ?:
            dataValueStore.selectWhere(whereClause).applyChanges()
        }


    /**
     * Accepted aggregationTypes:
     * - [AggregationType.SUM]
     * - [AggregationType.AVERAGE]
     * - [AggregationType.MAX]
     * - [AggregationType.MIN]
     *
     * If the user does not provide an aggregation type or the aggregation type is not accepted, it must default
     * to [AggregationType.SUM]
     */
    fun withAggregationType(type: AggregationType): DataValueCalculator =
        apply {
            aggregationTypes = type
        }

    /**
     * It must return the evaluation of the existing data values using the parameters provided.
     * If there is no matching dataValues, it must return a 0.0.
     * If any value cannot be converted to float, it must return a 0.0.
     */
    fun evaluate(): Float {

        listOfDataValue ?: dataValueStore.selectAll().applyChanges()

        return when (aggregationTypes) {
            AggregationType.SUM -> {
                add(listOfDataValue)
            }
            AggregationType.AVERAGE -> {
                average(listOfDataValue)
            }
            AggregationType.MAX -> {
                max(listOfDataValue)
            }
            AggregationType.MIN -> {
                min(listOfDataValue)
            }
            else -> {
                add(listOfDataValue)
            }
        }

    }

    private fun List<DataValue>?.applyChanges(){
        listOfDataValue = null
        this?.let { listOfDataValue = it.toMutableList() }
    }

    override fun clear() {
        listOfDataValue = null
        aggregationTypes = AggregationType.SUM
    }


}

abstract class CalculatorOperation{

    abstract fun clear()

     fun add(dataList: List<DataValue>?): Float{
        var result = 0f
        var floatValue = 0f
         dataList?.forEach { dataValue ->
            try {
                floatValue = dataValue.value()?.toFloat()!!
            }catch (e: Exception){
                return 0f
            }
            result += floatValue
        }
         clear()
        return result
    }

     fun average(dataList: List<DataValue>?): Float{
        var result = 0f
        var floatValue = 0f
        val total = dataList?.size ?: 1
         dataList?.forEach { dataValue ->
            try {
                floatValue = dataValue.value()?.toFloat()!!
            }catch (e: Exception){
                return 0f
            }
            result += floatValue
        }

         clear()
         return result / total
    }

     fun max(dataList: List<DataValue>?): Float{
        return try {
            val maxValue = dataList?.maxBy { it.value()!!.toFloat() }
            clear()
            maxValue?.value()?.toFloat() ?: 0f
        }catch (e: Exception){
            return 0f
        }
    }

     fun min(dataList: List<DataValue>?): Float{
        return try {
            val maxValue = dataList?.minBy { it.value()!!.toFloat() }
            clear()
            maxValue?.value()?.toFloat() ?: 0f
        }catch (e: Exception){
            return 0f
        }
    }
}
