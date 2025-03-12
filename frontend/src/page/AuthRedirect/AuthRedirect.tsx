import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

import React from 'react'
import { setUserInfo } from "../../store/store";
import { useDispatch } from "react-redux";

const AuthRedirect = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const dispatch = useDispatch();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const accessToken = params.get("accessToken");
    const email = params.get("email");
    const nickname = params.get("nickname");
    const loginId = params.get("loginId");
    
    const oAuth2User = JSON.stringify({ email, nickname, loginId});

    if (accessToken && oAuth2User) {
      localStorage.setItem("accessToken", accessToken);
      dispatch(setUserInfo(JSON.parse(oAuth2User)));
      navigate("/"); // 저장 후 리다이렉트
    } 
  }, [location, navigate]);

  return (
    <div>
      
    </div>
  )
}

export default AuthRedirect;