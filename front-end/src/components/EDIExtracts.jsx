import React, { useEffect, useState, useContext } from "react";
import { Empty, Layout, message, Spin } from "antd";
import { PageHeader } from '@ant-design/pro-layout';
import axios from "axios";
import Extract from "./Extract";
import { UserContext } from "../Contexts/UserContext";
import { useHistory } from "react-router-dom";

import "../styles/edi-extracts.css";

const endpoint = process.env.REACT_APP_API_URL;
const { Content } = Layout;

function EDIExtracts(props) {
  const data = props.location.state.data;
  const [extracts, setExtracts] = useState([]);
  const [loading, setLoading] = useState(false);
  const { authUser } = useContext(UserContext);
  const history = useHistory();

  const loadExtracts = (ids) => {
    setLoading(true);
    axios
      .post(`${endpoint}/data/extract-edi`, ids)
      .then((res) => {
        setExtracts(res.data);
        setLoading(false);
      })
      .catch((err) => {
        console.log(err);
        message.error("error while loading data");
        setLoading(false);
      });
  };
  
  useEffect(() => {
    if (!authUser.authenticated) {
      history.push({ pathname: "/" });
    }

    if (data) {
      console.log(data);
      loadExtracts(data.ids);
    }
    
  }, [data, authUser, history]);

  return (
    <Layout className="extracts">
      <PageHeader
        title="EDI Extracts"
        subTitle="for selected claims"
        onBack={() => history.push({ pathname: props.location.state.back })}
      />
      {loading && <Spin tip="please wait..." size="large" />}
      <Content className="extracts__extract">
        {extracts && extracts.length > 0 ? (
          extracts.map((ext, index) => (
            <Extract
              runExtract={ext}
              members={data.members}
              providers={data.providers}
              key={index}
              run={data.run}
            />
          ))
        ) : (
          <Empty description="No Extracts Found" />
        )}
      </Content>
    </Layout>
  );
}

export default EDIExtracts;
