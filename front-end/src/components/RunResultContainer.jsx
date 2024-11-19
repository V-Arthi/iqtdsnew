import React, { useState, useEffect } from "react";
import { useHistory } from "react-router-dom";
import { Tabs, Button, Table, Empty } from "antd";
import {
  SolutionOutlined,
  UserOutlined,
  TeamOutlined,
} from "@ant-design/icons";

const { TabPane } = Tabs;

function RunResultContainer({ run }) {
  const [picked, setPicked] = useState([]);
  const [members, setMembers] = useState([]);
  const [providers, setProviders] = useState([]);
  
  const history = useHistory();

  const rowSelection = {
    onChange: (selectedRowKeys, selectedRows) => {
      setPicked(
        selectedRows
          .map((d) => d.ID)
          .filter((item, index, ar) => ar.indexOf(item) === index)
      );
      //console.log(selectedRows)
    },
  };

  useEffect(() => {
    // console.clear();
    
  }, []);

  useEffect(() => {
    // console.log(run.testCondition.claimType);
    if (run.memberResults && run.memberResults.length > 0) {
      setMembers(
        run.memberResults.map(
          (mr) => `${mr.data["Subscriber ID"]}-${mr.data.Suffix}`
        )
      );
    }

    if (run.providerResults && run.providerResults.length > 0) {
      setProviders(run.providerResults.map((pr) => pr.data.ID));
    }
  }, [run]);

  const hasSelected = picked.length > 0;

  const extractEDI = () => {
    const data = {
      ids: picked.map((i) => ({ id: i, env: run.env, runId: run.id })),
      members,
      providers,
      run,
    };

    history.push({
      pathname: "/result/extract",
      state: { data, back: history.location.pathname },
    });
  };

  const triggerOnline = () => {
    const data = {
      claims: run.claimResults
        .map((claim) => claim.data.ID)
        .filter((item, index, ar) => ar.indexOf(item) === index),
      runID: run.id,
      testId: run.testCondition.testId,
      env: run.env,
      jenkinsRuns: run.jenkinsRuns,
      claimFilters: run.testCondition.claimFilters,
    };

    history.push({
      pathname: `/run/claim-online/${run.id}` ,
      state: { data, back: history.location.pathname },
    });
  };

  return (
    <Tabs defaultActiveKey="claims">
      <TabPane
        tab={
          <span>
            <SolutionOutlined />
            Claims
          </span>
        }
        key="claims"
      >
        <div className="run_result_table">
          {run.claimResults && run.claimResults.length > 0 ? (
            <Table
              rowSelection={{
                type: "checkbox",
                ...rowSelection,
              }}
              title={() => (
                <div className="result_table_header">
                  <div className="result_table_title">
                    <span>Claims</span>
                  </div>

                  <div className="result_table_actions">
                    <span style={{ marginRight: 10 }}>
                      {hasSelected &&
                        `Selected ${picked.length} distinct item(s)`}
                    </span>
                    <Button
                      disabled={!hasSelected}
                      type="primary"
                      onClick={extractEDI}
                      style={{ marginRight: 10 }}
                    >
                      Extract EDI
                    </Button>
                    <Button type="primary" onClick={triggerOnline}>
                      Trigger Online Claim Creation
                    </Button>
                  </div>
                </div>
              )}
              small
              columns={Object.keys(run.claimResults[0].data).map((c) => ({
                title: c,
                dataIndex: c,
                width: 75,
              }))}
              dataSource={run.claimResults.map((r) => ({
                ...r.data,
                key: r.id,
              }))}
              scroll={{ x: 600 }}
              footer={() =>
                `Requested : ${run.recordLength} | Found : ${run.claimResults.length}`
              }
            />
          ) : (
            <Empty description={<span>No data found for Claims</span>} />
          )}
        </div>
      </TabPane>
      <TabPane
        tab={
          <span>
            <UserOutlined />
            Members
          </span>
        }
        key="members"
      >
        <div className="run_result_table">
          {run.memberResults && run.memberResults.length > 0 ? (
            <Table
              title={() => (
                <div className="result_table_header">
                  <div className="result_table_title">
                    <span>Members</span>
                  </div>
                  <span style={{ color: "#777" }}>
                    {" "}
                    * This is for Member Override Only
                  </span>
                </div>
              )}
              small
              columns={Object.keys(run.memberResults[0].data).map((c) => ({
                title: c,
                dataIndex: c,
                width: 75,
              }))}
              dataSource={run.memberResults.map((r) => ({
                ...r.data,
                key: r.id,
              }))}
              scroll={{ x: 600 }}
              footer={() =>
                `Requested : ${run.recordLength} | Found : ${run.memberResults.length}`
              }
            />
          ) : (
            <Empty description={<span>No data found for Members</span>} />
          )}
        </div>
      </TabPane>
      <TabPane
        tab={
          <span>
            <TeamOutlined />
            Providers
          </span>
        }
        key="providers"
      >
        <div className="run_result_table">
          {run.providerResults && run.providerResults.length > 0 ? (
            <Table
              title={() => (
                <div className="result_table_header">
                  <div className="result_table_title">
                    <span>Providers</span>
                  </div>

                  <span style={{ color: "#777" }}>
                    {" "}
                    * This is for Provider Override Only
                  </span>
                </div>
              )}
              small
              columns={Object.keys(run.providerResults[0].data).map((c) => ({
                title: c,
                dataIndex: c,
                width: 75,
              }))}
              dataSource={run.providerResults.map((r) => ({
                ...r.data,
                key: r.id,
              }))}
              scroll={{ x: 600 }}
              footer={() =>
                `Requested : ${run.recordLength} | Found : ${run.providerResults.length}`
              }
            />
          ) : (
            <Empty description={<span>No data found for Members</span>} />
          )}
        </div>
      </TabPane>
    </Tabs>
  );
}

export default RunResultContainer;
