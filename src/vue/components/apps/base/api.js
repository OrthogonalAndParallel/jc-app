export const apiClient = {

    baseURL: process.env.NODE_ENV === 'development' ? '/api' : 'http://localhost:8001',

    async request(url, options = {}) {
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const config = { ...defaultOptions, ...options };
        const fullUrl = this.baseURL + url;

        try {
            const response = await fetch(fullUrl, config);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                return await response.text();
            }
        } catch (error) {
            console.error('API 请求失败:', error);
            throw error;
        }
    },

    get(url, params = {}) {
        let fullUrl = url;

        if (Object.keys(params).length > 0) {
            const searchParams = new URLSearchParams(params);
            fullUrl += (url.includes('?') ? '&' : '?') + searchParams.toString();
        }

        return this.request(fullUrl, { method: 'GET' });
    },

    post(url, data = {}) {
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    },

    put(url, data = {}) {
        return this.request(url, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    },

    delete(url, data = {}) {
        const options = { method: 'DELETE' };

        if (Object.keys(data).length > 0) {
            options.body = JSON.stringify(data);
        }

        return this.request(url, options);
    },

    // 可选：添加设置方法
    setBaseURL(baseURL) {
        this.baseURL = baseURL;
    }
}