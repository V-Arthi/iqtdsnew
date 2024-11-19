import React, { useEffect, useState, useContext } from "react";
import { Layout, Divider, message, Result, Tabs } from "antd";
import { PageHeader } from '@ant-design/pro-layout';
import HeaderSection from "./HeaderSection";
import { Redirect } from "react-router-dom";
import axios from "axios";
import { UserContext } from "../Contexts/UserContext";
import "../styles/viewJob.css";
import { v4 as uuid } from "uuid";
import { useHistory } from "react-router-dom";
import RunResultContainer from "./RunResultContainer";

const { Content } = Layout;

const { TabPane } = Tabs;

const endpoint = process.env.REACT_APP_API_URL;

function ViewJob(props) {
  
  const { authUser } = useContext(UserContext);

  const jobId = props && props.match.params.id;

  const [job, setJob] = useState({});

  const history = useHistory();

  useEffect(() => {
    if (authUser.authenticated) {
      jobId &&
        axios
          .get(`${endpoint}/monitor/jobs/${jobId}`)
          .then((res) => {
            message.info("Data Loaded", 1);
            console.clear();
            // console.log(res.data)
            setJob(res.data);
          })
          .catch((err) => message.error("Error while loading job"));
    }
  }, [jobId, authUser.authenticated]);

  return (
    <>
      {authUser.authenticated && props && job ? (
        <Layout className="job__container">
          <PageHeader
            title="View Job Results"
            style={{ alignSelf: "flex-start" }}
            onBack={() => {
              history.push({ pathname: "/jobs" });
            }}
          />
          <HeaderSection type="job" entity={job} />
          <Content className="run__container">
            <Tabs defaultActiveKey="1">
              {job.runs &&
                job.runs.map((r, index) => (
                  <TabPane tab={`${index + 1} - Run #${r.id}`} key={index + 1}>
                    {r.testCondition ? (
                      <Layout
                        className="run__container__run"
                        key={uuid()}
                        id={`${r.id}_run_result`}
                      >
                        <HeaderSection type="run" entity={r} />
                        <Divider className="run__container__divider" />
                        <RunResultContainer run={r} />
                      </Layout>
                    ) : (
                      <Result
                        status="404"
                        title="Run Result Not Found"
                        subTitle={`Attached 'Test Condition' for Run '#${r.id}' might have been changed/deleted`}
                      />
                    )}
                  </TabPane>
                ))}
            </Tabs>
            {/* <RouterLink to={{pathname:'/jobs'}} className="ant-btn btn-back">Back</RouterLink> */}
          </Content>
        </Layout>
      ) : (
        <Redirect to={{ pathname: "/" }} />
      )}
    </>
  );
}

export default ViewJob;
