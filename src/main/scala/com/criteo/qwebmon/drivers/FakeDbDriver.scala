package com.criteo.qwebmon.drivers

import com.criteo.qwebmon.{RunningQuery, DbDriver}

import scala.util.Random

class FakeDbDriver(val name: String) extends DbDriver {

  private def randInt = Random.nextInt(100)

  override def runningQueries: Seq[RunningQuery] = Seq(
      RunningQuery("alexanderhamilton", randInt, "select 1", "192.168.0.23"),
      RunningQuery("johndoe", 350, "select distinct 1", "127.0.0.1"),
      RunningQuery("asmith", randInt, "select a + b", "192.168.13.11"),
      RunningQuery("foobar", randInt, "select foobar from foo join bar where baz = 344400 and bim between 12000 and 400000 group by foobar", "127.0.0.1"),
      RunningQuery("janedoe", randInt, """with mone as ( Select a.client_id ,b.client_name ,displays ,min(day) as start_date From    datamart.fact_client_stats_daily a join    datamart.dim_client b on    a.client_id  = b.client_id where    1=1 and    displays > 50 and    b.client_country_code in ('DE','PL','AT','CH') and    b.ranking = 'MMS' group by  1,2 order by   start_date desc limit    10), zero as ( select client_name From    mone where    start_date between current_date - 7 and current_date), one as ( select d2.client_name ,count(distinct(user_id_fast)) / count(distinct(day)) * 30 as avg_uv from     datamart.fact_events f join     datamart.dim_merchant d on     f.merchant_id = d.merchant_id join     datamart.dim_client d2 on     d2.client_id = d.most_displayed_client_id join    zero on    zero.client_name = d2.client_name group by   1 ), two as ( select client_name ,count(distinct(transaction_id)) / count(distinct(day)) * 30 as avg_trans from datamart.fact_events_transactions f join datamart.dim_merchant d on f.merchant_id = d.merchant_id join datamart.dim_client d2 on d2.client_id = d.most_displayed_client_id group by 1), three as ( select client_name , (sum(revenue) - sum(tac*EXtac.rate)) / count(distinct f.day) * 30 as avg_gross from datamart.fact_client_stats_daily f join lta.mms_dim_client CL on f.client_id = cl.client_id LEFT JOIN datamart.fact_cpop_exchange_rates_daily EXtac on F.zone_currency_id = EXtac.source_currency_id and F.day = EXtac.day and EXtac.destination_currency_id = CL.currency_id group by 1) Select one.client_name ,one.avg_uv ,two.avg_trans ,three.avg_gross from    one join    two on    one.client_name = two.client_name join    three on    one.client_name = three.client_name | f""", "192.168.13.11")
    ).sortWith { case (a, b) => a.runSeconds > b.runSeconds }

}
