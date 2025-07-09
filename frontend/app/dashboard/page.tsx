'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { CheckCircleIcon, DocumentCheckIcon, ClockIcon } from '@heroicons/react/24/outline'

interface SubmissionResult {
  status: string
  message: string
  applicationId: number
  accountNumber: string
  applicationStatus: string
  submissionDate: string
}

export default function DashboardPage() {
  const router = useRouter()
  const [submissionResult, setSubmissionResult] = useState<SubmissionResult | null>(null)
  const [userSession, setUserSession] = useState<{email: string; loginTime: string} | null>(null)

  useEffect(() => {
    // Check if user is logged in
    const session = sessionStorage.getItem('userSession')
    if (!session) {
      router.push('/')
      return
    }

    setUserSession(JSON.parse(session))

    // Load submission result
    const result = sessionStorage.getItem('submissionResult')
    if (result) {
      setSubmissionResult(JSON.parse(result))
    }
  }, [router])

  const handleStartNewApplication = () => {
    // Clear session data
    sessionStorage.removeItem('kycFormData')
    sessionStorage.removeItem('submissionResult')

    // Redirect to KYC form
    router.push('/kyc-form')
  }

  const handleLogout = () => {
    // Clear all session data
    sessionStorage.removeItem('userSession')
    sessionStorage.removeItem('kycFormData')
    sessionStorage.removeItem('submissionResult')

    // Redirect to login
    router.push('/')
  }

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'Submitted':
        return <CheckCircleIcon className="h-12 w-12 text-green-500" />
      case 'Draft':
        return <ClockIcon className="h-12 w-12 text-yellow-500" />
      default:
        return <DocumentCheckIcon className="h-12 w-12 text-blue-500" />
    }
  }

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'Submitted':
        return 'text-green-600 bg-green-50'
      case 'Draft':
        return 'text-yellow-600 bg-yellow-50'
      default:
        return 'text-blue-600 bg-blue-50'
    }
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-mpesa-green-600 py-3 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center">
            <div className="text-white font-bold text-xl">M-PESA</div>
            <div className="text-white ml-2 text-sm">Safaricom</div>
          </div>
          <div className="flex items-center space-x-4">
            <span className="text-white text-sm">Welcome, {userSession?.email}</span>
            <button
              onClick={handleLogout}
              className="text-white hover:text-green-200 text-sm font-medium"
            >
              LOGOUT
            </button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-2 text-gray-600">Welcome to your M-PESA Acquisition Portal dashboard</p>
        </div>

        {/* Submission Result */}
        {submissionResult && (
          <div className="bg-white rounded-lg shadow-md p-6 mb-8">
            <div className="text-center">
              <div className="mx-auto flex items-center justify-center">
                {getStatusIcon(submissionResult.applicationStatus)}
              </div>
              <h3 className="mt-4 text-lg font-semibold text-gray-900">
                Application {submissionResult.applicationStatus}
              </h3>
              <p className="mt-2 text-gray-600">{submissionResult.message}</p>

              {/* Application Details */}
              <div className="mt-6 bg-gray-50 rounded-lg p-4">
                <div className="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <span className="font-medium text-gray-700">Application ID:</span>
                    <span className="ml-2 text-gray-900">{submissionResult.applicationId}</span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Account Number:</span>
                    <span className="ml-2 text-gray-900">{submissionResult.accountNumber}</span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Status:</span>
                    <span className={`ml-2 px-2 py-1 rounded-full text-xs font-medium ${getStatusColor(submissionResult.applicationStatus)}`}>
                      {submissionResult.applicationStatus}
                    </span>
                  </div>
                  <div>
                    <span className="font-medium text-gray-700">Submission Date:</span>
                    <span className="ml-2 text-gray-900">
                      {new Date(submissionResult.submissionDate).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Quick Actions */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-8">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <button
              onClick={handleStartNewApplication}
              className="btn-mpesa p-4 text-left"
            >
              <div className="flex items-center">
                <DocumentCheckIcon className="h-8 w-8 text-white mr-3" />
                <div>
                  <div className="font-medium">New Application</div>
                  <div className="text-sm opacity-90">Start a new M-PESA application</div>
                </div>
              </div>
            </button>
            <button
              onClick={() => window.location.reload()}
              className="btn-mpesa-outline p-4 text-left"
            >
              <div className="flex items-center">
                <ClockIcon className="h-8 w-8 text-mpesa-green-600 mr-3" />
                <div>
                  <div className="font-medium">Refresh Status</div>
                  <div className="text-sm opacity-70">Check application status</div>
                </div>
              </div>
            </button>
          </div>
        </div>

        {/* Application Status Guide */}
        <div className="bg-white rounded-lg shadow-md p-6">
          <h3 className="text-lg font-semibold text-gray-900 mb-4">Application Status Guide</h3>
          <div className="space-y-4">
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <ClockIcon className="h-6 w-6 text-yellow-500" />
              </div>
              <div className="ml-3">
                <h4 className="font-medium text-gray-900">Draft</h4>
                <p className="text-sm text-gray-600">
                  Application has been saved as draft. You can continue editing and submit when ready.
                </p>
              </div>
            </div>
            <div className="flex items-start">
              <div className="flex-shrink-0">
                <CheckCircleIcon className="h-6 w-6 text-green-500" />
              </div>
              <div className="ml-3">
                <h4 className="font-medium text-gray-900">Submitted</h4>
                <p className="text-sm text-gray-600">
                  Application has been successfully submitted and is being processed.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
