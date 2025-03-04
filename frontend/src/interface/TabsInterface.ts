export interface TabInterface{
    tabId: number;
    fileId: number;
    name: string;
    language: string;
    content: string;
    // 데이터는 s3에서 가져올건데 어떻게 해야하는지 지금은 모르겠다.
    // 백엔드 구현후에 해봐야할듯
}

export interface TabInterfaceArr{
    tabArr: TabInterface[];
}