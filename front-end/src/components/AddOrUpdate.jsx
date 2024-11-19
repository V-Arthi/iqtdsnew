import React, { useEffect, useState, useContext } from "react";
import { useParams, useHistory } from "react-router-dom";
import { UserContext } from "../Contexts/UserContext";
import axios from "axios";
import { Button, message, Radio, Spin} from "antd";
import TestHeader from "./TestHeader";
import FilterForm from "./FilterForm";
import Filters from "./Filters";
import { SaveOutlined, ReloadOutlined,FilterOutlined } from "@ant-design/icons";

import "../styles/add-update.css";
import TestTags from "./TestTags";

const endpoint = process.env.REACT_APP_API_URL;
const emptyTest = {
  testName: "",
  claimInputType: "",
  claimType: "",
  tags: "",
  providerType: "IN",
  claimFilters: [],
  memberFilters: [],
  providerFilters: [],
};

function AddOrUpdate() {
  const { authUser } = useContext(UserContext);
  const [test, setTest] = useState(emptyTest);
  const { id } = useParams();
  const [loading, setLoading] = useState(false);
  const [filterFlag, setfilterFlag] = useState(false);
  const [btnText, setBtnText] = useState("Save");

  const history = useHistory();

  

  useEffect(() => {
    if (id) {
      axios
        .get(`${endpoint}/condition/${id}`)
        .then((res) => setTest(res.data))
        .catch((err) =>
          message.error(`error while loading data for test condition '${id}`, 2)
        );

      setBtnText("Update");
    }
  }, [id]);

  const removeFilter = (filter, filtername) => {
    const test_copy = { ...test };
    const filters_copy = [...test_copy[filtername]];
    test_copy[filtername] = filters_copy.filter((f) => f !== filter);
    setTest(test_copy);
  };

  const handleProviderTypeChange = (e) => {
    const test_copy = { ...test };
    test_copy["providerType"] = e.target.value;
    setTest(test_copy);
  };

  const handleFilterBtn = (e) => {
    if(filterFlag){
      setfilterFlag(false);
    }else{
      setfilterFlag(true);
    }
    console.log(test.claimType)
  }

  const handleSave = (e) => {
    if (
      !(
        test.testName &&
        test.claimInputType &&
        test.claimType
      )
    ) {
      message.error("All headers fields are mandatory");
      return;
    }

    if (test.claimFilters.length === 0) {
      message.error("Test condition should have atleast one claim filter");
      return;
    }
    setLoading(true);

    if (id) {
      /*update existing condition */
      axios
        .put(`${endpoint}/condition/update/${id}`, test)
        .then((response) => {
          message.success(response.data);
          setLoading(false);
          history.push("/design");
        })
        .catch((err) => {
          message.error(err.response.data);
          setLoading(false);
        });
    } else {
      /*add new condition */
      axios
        .post(`${endpoint}/condition/add`, test)
        .then((response) => {
          message.success(response.data);
          setLoading(false);
          history.push("/design");
        })
        .catch((error) => {
          message.error(error.response.data);
          console.log(error);
          setLoading(false);
        });
    }
  };

  return (
    authUser &&
    authUser.authenticated && (
      <div className="add_udpate" id="add_update">
        {loading && (
          <Spin
            tip="please wait..."
            size="large"
            wrapperClassName="add_update"
            className="custom-spin"
          />
        )}
        <TestHeader setTest={setTest} test={test} className="test_header" />
        <Button
            icon={<FilterOutlined />}
            onClick={handleFilterBtn}
            className="btn_group"
            type="primary"
            style={{ marginLeft: "auto", marginTop: 5 , display:"flex"}}
          >
            Filter
          </Button>
          <br />
          { filterFlag ? (
            <div>
            <main className="filters_main">
            <div className="filter_section" name="claim_filters">
              <div className="filter_title">
                <p>claim filters</p>
              </div>
              {console.log("Claim Type: "+ test.claimType)}
              <FilterForm test={test} setTest={setTest} filterType={test.claimType} />
              <Filters
                filters={test.claimFilters}
                name="claimFilters"
                removeFn={removeFilter}
                fillers={[
                  "Claims received within 12 months on run date",
                  "Claims that are not in 15 status",
                ]}
              />
            </div>
  
            <div className="filter_section" name="member_filters">
              <div className="filter_title">
                <p>member filters</p>
              </div>
  
              <FilterForm test={test} setTest={setTest} filterType="members" />
              <Filters
                filters={test.memberFilters}
                name="memberFilters"
                removeFn={removeFilter}
                fillers={["Effective Member On Run Date"]}
              />
            </div>
  
            <div className="filter_section" name="provider_filters">
              <div className="filter_title">
                <p>provider filters</p>
              </div>
              <FilterForm test={test} setTest={setTest} filterType="providers" />
              <Radio.Group
                defaultValue={test.providerType || "IN"}
                buttonStyle="solid"
                className="provider_type_radio"
              >
                <Radio.Button
                  value="IN"
                  onClick={handleProviderTypeChange}
                  className="provider_type_radio_item"
                >
                  PAR
                </Radio.Button>
                <Radio.Button
                  value="OUT"
                  onClick={handleProviderTypeChange}
                  className="provider_type_radio_item"
                >
                  NON PAR
                </Radio.Button>
              </Radio.Group>
              <Filters
                filters={test.providerFilters}
                name="providerFilters"
                removeFn={removeFilter}
                fillers={["Effective Provider On Run Date"]}
              />
            </div>
          </main>
          <footer className="add_update_footer">
            <TestTags test={test} setTest={setTest} />
            <div className="footer_action">
              <Button
                type="primary"
                size="large"
                icon={<SaveOutlined />}
                disabled={loading}
                onClick={handleSave}
                style={{ marginRight: 10 }}
              >
                {btnText}
              </Button>
              <Button
                type="default"
                size="large"
                icon={<ReloadOutlined />}
                disabled={loading}
                href={"#/design"}
              >
                Cancel
              </Button>
            </div>
          </footer>
          </div>
          ) : null
          }
      </div> 
    )
  );
}

export default AddOrUpdate;
