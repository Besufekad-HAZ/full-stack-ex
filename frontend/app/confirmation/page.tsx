'use client'

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import axios from 'axios'
import { CheckCircleIcon, ExclamationTriangleIcon } from '@heroicons/react/24/outline'

interface FormData {
  bankId: string
  branchId: string
  bankName: string
  branchName: string
  accountName: string
  accountNumber: string
  proofOfBankAccount: string
}

export default function ConfirmationPage() {
  const router = useRouter()
  const [formData, setFormData] = useState<FormData | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  useEffect(() => {
    // Check if user is logged in
    const userSession = sessionStorage.getItem('userSession')
    if (!userSession) {
      router.push('/')
      return
    }

    // Load form data from session storage
    const savedFormData = sessionStorage.getItem('kycFormData')
    if (savedFormData) {
      setFormData(JSON.parse(savedFormData))
    } else {
      // If no form data, redirect back to KYC form
      router.push('/kyc-form')
    }
  }, [router])

  const handleSubmit = async (status: 'SUBMITTED' | 'DRAFT') => {
    if (!formData) return

    setIsSubmitting(true)
    setError('')
    setSuccess('')

    try {
      const submissionData = {
        bankId: parseInt(formData.bankId),
        branchId: parseInt(formData.branchId),
        bankName: formData.bankName,
        branchName: formData.branchName,
        accountName: formData.accountName,
        accountNumber: formData.accountNumber,
        proofOfBankAccount: formData.proofOfBankAccount,
        status: status
      }

      const response = await axios.post('http://localhost:8080/api/applications/submit', submissionData)

      if (response.data.status === 'SUCCESS') {
        setSuccess(response.data.message)

        // Store submission result
        sessionStorage.setItem('submissionResult', JSON.stringify(response.data))

        // Redirect to dashboard after a short delay
        setTimeout(() => {
          router.push('/dashboard')
        }, 2000)
      } else {
        setError(response.data.message || 'Submission failed')
      }
    } catch (error: unknown) {
      const errorMessage = error instanceof Error ? error.message : 'An error occurred during submission. Please try again.'

      if (error && typeof error === 'object' && 'response' in error) {
        const axiosError = error as { response?: { data?: { message?: string } } }
        if (axiosError.response?.data?.message) {
          setError(axiosError.response.data.message)
        } else {
          setError(errorMessage)
        }
      } else {
        setError(errorMessage)
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleBack = () => {
    router.push('/kyc-form')
  }

  if (!formData) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-green-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading...</p>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-green-600 py-3 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between max-w-7xl mx-auto">
          <div className="flex items-center">
            <div className="text-white font-bold text-xl">M-PESA</div>
            <div className="text-white ml-2 text-sm">Safaricom</div>
          </div>
          <div className="flex space-x-4">
            <button className="text-white hover:text-green-200 text-sm font-medium">APPLY</button>
            <button className="text-white hover:text-green-200 text-sm font-medium">RECOMMEND</button>
            <button className="text-white hover:text-green-200 text-sm font-medium">LOGIN</button>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-4xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          {/* Header */}
          <div className="bg-gray-50 px-6 py-4 border-b border-gray-200">
            <h2 className="text-2xl font-bold text-gray-900">FUND WITHDRAW OPTION</h2>
          </div>

          {/* Content */}
          <div className="p-6">
            <div className="border-l-4 border-green-600 pl-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-6">
                Please review your information before submitting
              </h3>

              {/* Review Information */}
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-8">
                  <div>
                    <h4 className="font-medium text-gray-700 mb-2">BANK NAME:</h4>
                    <p className="text-gray-900">{formData.bankName}</p>
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-700 mb-2">BANK BRANCH NAME:</h4>
                    <p className="text-gray-900">{formData.branchName}</p>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-8">
                  <div>
                    <h4 className="font-medium text-gray-700 mb-2">ACCOUNT NAME:</h4>
                    <p className="text-gray-900">{formData.accountName}</p>
                  </div>
                  <div>
                    <h4 className="font-medium text-gray-700 mb-2">ACCOUNT NUMBER:</h4>
                    <p className="text-gray-900">{formData.accountNumber}</p>
                  </div>
                </div>

                <div>
                  <h4 className="font-medium text-gray-700 mb-2">PROOF OF BANK ACCOUNT:</h4>
                  <p className="text-gray-900">{formData.proofOfBankAccount}</p>
                </div>
              </div>
            </div>

            {/* Status Messages */}
            {error && (
              <div className="mt-6 rounded-md bg-red-50 p-4">
                <div className="flex">
                  <ExclamationTriangleIcon className="h-5 w-5 text-red-400" />
                  <div className="ml-3">
                    <p className="text-sm font-medium text-red-800">{error}</p>
                  </div>
                </div>
              </div>
            )}

            {success && (
              <div className="mt-6 rounded-md bg-green-50 p-4">
                <div className="flex">
                  <CheckCircleIcon className="h-5 w-5 text-green-400" />
                  <div className="ml-3">
                    <p className="text-sm font-medium text-green-800">{success}</p>
                  </div>
                </div>
              </div>
            )}

            {/* Action Buttons */}
            <div className="flex justify-end space-x-4 pt-8 border-t border-gray-200 mt-8">
              <button
                type="button"
                onClick={handleBack}
                disabled={isSubmitting}
                className="btn-mpesa-outline px-6 py-2"
              >
                Back
              </button>
              <button
                type="button"
                onClick={() => handleSubmit('DRAFT')}
                disabled={isSubmitting}
                className="px-6 py-2 border border-gray-300 rounded-md text-gray-700 bg-white hover:bg-gray-50 font-medium transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {isSubmitting ? 'Saving...' : 'Save as Draft'}
              </button>
              <button
                type="button"
                onClick={() => handleSubmit('SUBMITTED')}
                disabled={isSubmitting}
                className="btn-mpesa px-6 py-2"
              >
                {isSubmitting ? 'Submitting...' : 'Submit'}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
