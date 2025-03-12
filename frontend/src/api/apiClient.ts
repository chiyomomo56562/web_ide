import axios from 'axios';

const apiClient = axios.create({
//   baseURL: 'http://localhost:3000', // 백엔드 서버 주소
  headers: { 'Content-Type': 'application/json' },
  withCredentials: true,
});

// axios 인터셉터를 통해 토큰 갱신 로직도 추가 가능
apiClient.interceptors.response.use(
  response => response,
  async error => {
    const originalRequest = error.config
    if (error.response && error.response.status === 401 && !error.config._retry) {
      error.config._retry = true;
      try {
        // refresh token을 사용하여 새로운 access token 요청
        const refreshResponse = await axios.post(
            '/api/auth/refresh',
            {}, // payload 없이 빈 객체 전달
            { withCredentials: true }
        );
        const { accessToken, refreshToken } = refreshResponse.data;
        localStorage.setItem('accessToken', accessToken);
        // 새로운 토큰을 localStorage에 저장하고, 헤더 갱신 후 원래 요청 재시도
        originalRequest.headers['Authorization'] = 'Bearer ' + accessToken;
        return axios(originalRequest);
        }
        catch (refreshError) {
        console.error('토큰 갱신 실패:', refreshError);
        localStorage.removeItem('accessToken');
        //refresh token은 삭제를 못하는데.....
        return Promise.reject(refreshError);
        }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
