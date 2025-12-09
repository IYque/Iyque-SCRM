declare module '@/utils/request' {
  interface RequestMethods {
    get<T = any>(url: string, params?: any, config?: any): Promise<T>
    post<T = any>(url: string, data?: any, config?: any): Promise<T>
    put<T = any>(url: string, data?: any, config?: any): Promise<T>
    delete<T = any>(url: string, params?: any, config?: any): Promise<T>
    delt<T = any>(url: string, params?: any, config?: any): Promise<T>
    del<T = any>(url: string, params?: any, config?: any): Promise<T>
  }

  const request: RequestMethods
  export default request
}