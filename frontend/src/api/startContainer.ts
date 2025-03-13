import apiClient from "./apiClient";

const startContainer = async (projectId:number) => {
    try {
        const response = await apiClient.post("/api/start-ide", { projectId });
        console.log("컨테이너 실행 성공:", response.data);
        return response.data; // 백엔드에서 반환된 컨테이너 정보 (예: id, url 등)
    } catch (error) {
        console.error("컨테이너 실행 오류:", error);
        return null;
    }
};

export default startContainer;